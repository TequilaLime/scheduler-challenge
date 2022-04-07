/* (C) 2022 */
package com.doodle.backendchallenge.service;

import com.doodle.backendchallenge.entity.MeetingEntity;
import com.doodle.backendchallenge.entity.SlotEntity;
import com.doodle.backendchallenge.exceptions.ConflictException;
import com.doodle.backendchallenge.exceptions.NotFoundException;
import com.doodle.backendchallenge.model.dto.SlotCollectionDto;
import com.doodle.backendchallenge.model.dto.SlotDto;
import com.doodle.backendchallenge.repository.SlotRepository;
import com.doodle.backendchallenge.specifications.OverlapsSpecifications;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class SlotService {

  private SlotRepository slotRepository;
  private ModelMapper modelMapper;

  @Transactional
  public SlotDto createSlot(SlotDto slotRequest) {
    Specification<SlotEntity> whereClause =
        Specification.where(
            OverlapsSpecifications.startAtOverlaps(slotRequest.getStartAt(), slotRequest.getEndAt())
                .or(
                    OverlapsSpecifications.startAtOverlaps(
                            slotRequest.getStartAt(), slotRequest.getEndAt())
                        .and(OverlapsSpecifications.endAtLess(slotRequest.getEndAt()))));
    List<SlotEntity> all = slotRepository.findAll(whereClause);
    if (!all.isEmpty()) { // slot overlaps
      log.error(
          "Slot request with startAt: {}, endAt: '{}' overlaps with another slot",
          slotRequest.getStartAt(),
          slotRequest.getEndAt().toString());
      throw new ConflictException("Slot overlaps");
    }
    // Otherwise create a slot
    SlotEntity savedSlotEntity = slotRepository.save(convertToEntity(slotRequest));
    return convertToDto(savedSlotEntity);
  }

  @Transactional
  public void releaseSlotFromMeeting(MeetingEntity meetingEntity) {
    SlotEntity slotEntity =
        SlotEntity.builder()
            .id(meetingEntity.getId())
            .startAt(meetingEntity.getStartAt())
            .endAt(meetingEntity.getEndAt())
            .build();
    slotRepository.save(slotEntity);
  }

  @Transactional(readOnly = true)
  public SlotCollectionDto readSlots(Integer offset, Integer limit) {
    Sort unsorted = Sort.unsorted();
    PageRequest pageRequest = PageRequest.of(offset, limit, unsorted);
    Page<SlotEntity> pageSlotEntities = slotRepository.findAll(pageRequest);
    return convertToDto(pageSlotEntities);
  }

  @Transactional(readOnly = true)
  public SlotDto readSlot(UUID id) {
    SlotEntity slotEntity =
        slotRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.error("slot with '{}' id is not found", id);
                  throw new NotFoundException("Slot not found");
                });

    return convertToDto(slotEntity);
  }

  @Transactional
  public void deleteSlot(UUID id) {
    if (!slotRepository.existsById(id)) {
      log.warn("Slot to be deleted with id {} is not found", id);
      throw new NotFoundException("Slot not found");
    }
    slotRepository.deleteById(id);
  }

  @Transactional
  public void deleteAllSlots() {
    slotRepository.deleteAll();
  }

  private SlotEntity convertToEntity(SlotDto slotDto) {
    SlotEntity slotEntity = modelMapper.map(slotDto, SlotEntity.class);
    slotEntity.setId(UUID.randomUUID());
    return slotEntity;
  }

  private SlotDto convertToDto(SlotEntity slotEntity) {
    return modelMapper.map(slotEntity, SlotDto.class);
  }

  private SlotCollectionDto convertToDto(Page<SlotEntity> page) {
    SlotCollectionDto slotCollectionDto = new SlotCollectionDto();
    slotCollectionDto.setItems(page.getContent().stream().map(this::convertToDto).toList());
    slotCollectionDto.setPage(page.getPageable().getOffset());
    slotCollectionDto.setPageSize((long) page.getNumberOfElements());
    slotCollectionDto.setTotalSize(page.getTotalElements());

    return slotCollectionDto;
  }
}
