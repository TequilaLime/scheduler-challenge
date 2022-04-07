/* (C) 2022 */
package com.doodle.backendchallenge.service;

import com.doodle.backendchallenge.entity.MeetingEntity;
import com.doodle.backendchallenge.exceptions.NotFoundException;
import com.doodle.backendchallenge.model.dto.MeetingCollectionDto;
import com.doodle.backendchallenge.model.dto.MeetingDto;
import com.doodle.backendchallenge.model.dto.SlotDto;
import com.doodle.backendchallenge.model.dto.UserDto;
import com.doodle.backendchallenge.repository.MeetingsRepository;
import com.doodle.backendchallenge.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class MeetingService {

  private MeetingsRepository meetingsRepository;
  private ModelMapper modelMapper;
  private SlotService slotService;
  private UserRepository userRepository;

  @Transactional
  public MeetingDto createMeeting(MeetingDto meetingDto) {
    SlotDto slotDto = slotService.readSlot(meetingDto.getSlotId());
    List<UUID> userIds =
        Optional.ofNullable(meetingDto.getParticipants()).orElse(new ArrayList<>()).stream()
            .map(UserDto::getId)
            .toList();
    MeetingEntity meetingEntity = convertToEntity(meetingDto, slotDto);
    if (!userIds.isEmpty()) {
      meetingEntity.setUserEntityList(userRepository.findAllById(userIds));
    }
    MeetingEntity savedMeeting = meetingsRepository.save(meetingEntity);
    slotService.deleteSlot(savedMeeting.getId());
    return convertToDto(savedMeeting);
  }

  @Transactional(readOnly = true)
  public MeetingCollectionDto readMeetings(Integer offset, Integer limit) {
    Sort unsorted = Sort.unsorted();
    PageRequest pageRequest = PageRequest.of(offset, limit, unsorted);
    Page<MeetingEntity> pageMeetingEntity = meetingsRepository.findAll(pageRequest);
    return convertToDto(pageMeetingEntity);
  }

  @Transactional(readOnly = true)
  public MeetingDto readMeeting(UUID id) {
    MeetingEntity meetingEntity =
        meetingsRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.error("meeting with '{}' id is not found", id);
                  throw new NotFoundException("Meeting not found");
                });

    return convertToDto(meetingEntity);
  }

  @Transactional
  public void deleteMeeting(UUID id) {
    if (!meetingsRepository.existsById(id)) {
      log.warn("Meeting to be deleted with id {} is not found", id);
      throw new NotFoundException("Meeting not found");
    }
    MeetingEntity meetingEntity = meetingsRepository.getById(id);
    slotService.releaseSlotFromMeeting(meetingEntity);
    meetingsRepository.deleteById(id);
  }

  @Transactional
  public void deleteAllMeetings() {
    meetingsRepository.deleteAll();
  }

  private MeetingEntity convertToEntity(MeetingDto meetingDto, SlotDto slotDto) {
    MeetingEntity meetingEntity = modelMapper.map(meetingDto, MeetingEntity.class);
    meetingEntity.setId(meetingDto.getSlotId());
    meetingEntity.setStartAt(slotDto.getStartAt());
    meetingEntity.setEndAt(slotDto.getEndAt());
    return meetingEntity;
  }

  private MeetingDto convertToDto(MeetingEntity meetingEntity) {
    MeetingDto meetingDto = modelMapper.map(meetingEntity, MeetingDto.class);
    meetingDto.setParticipants(
        meetingEntity.getUserEntityList().stream()
            .map(e -> modelMapper.map(e, UserDto.class))
            .toList());
    return meetingDto;
  }

  private MeetingCollectionDto convertToDto(Page<MeetingEntity> page) {
    MeetingCollectionDto meetingCollectionDto = new MeetingCollectionDto();
    meetingCollectionDto.setItems(page.getContent().stream().map(this::convertToDto).toList());
    meetingCollectionDto.setPage(page.getPageable().getOffset());
    meetingCollectionDto.setPageSize((long) page.getNumberOfElements());
    meetingCollectionDto.setTotalSize(page.getTotalElements());

    return meetingCollectionDto;
  }
}
