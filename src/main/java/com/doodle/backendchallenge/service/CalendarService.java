/* (C) 2022 */
package com.doodle.backendchallenge.service;

import com.doodle.backendchallenge.entity.CalendarDay;
import com.doodle.backendchallenge.entity.CalendarMonth;
import com.doodle.backendchallenge.model.dto.CalendarMonthDto;
import com.doodle.backendchallenge.model.dto.MeetingDto;
import com.doodle.backendchallenge.model.dto.SlotDto;
import com.doodle.backendchallenge.repository.CalendarRepository;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CalendarService {

  private CalendarRepository calendarRepository;
  private SlotService slotService;
  private MeetingService meetingService;
  private ModelMapper modelMapper;
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

  public void pushSlotIntoCalendar(UUID slotId) {
    log.debug("push slot to Redis");
    SlotDto slotDto = slotService.readSlot(slotId);
    String id = slotDto.getStartAt().format(FORMATTER); // get id
    int dayOfMonth = slotDto.getStartAt().getDayOfMonth();
    CalendarMonth month = findById(id);
    CalendarDay day = month.getCalendarDay(dayOfMonth);
    if (day != null) {
      day.getSlots().add(slotDto);
      Collections.sort(day.getSlots());
    } else {
      log.warn(
          "day of month is null, possible it's only just going to be initialized. id={}, dayOfMonth={}",
          id,
          dayOfMonth);
      CalendarDay build = CalendarDay.builder().slots(Collections.singletonList(slotDto)).build();
      month.getDays().put(dayOfMonth, build);
    }
    calendarRepository.save(month);
    log.debug("successfully pushed slot");

    // TODO delete later
    log.debug("try to get value");
    Optional<CalendarMonth> byId = calendarRepository.findById(id);
    log.debug("is value exists: " + byId.isPresent());
    byId.ifPresent(
        v -> {
          log.debug("the value is: " + v);
        });
  }

  public void pushMeetingIntoCalendar(UUID meetingId) {
    log.debug("push meeting to Redis");
    MeetingDto meetingDto = meetingService.readMeeting(meetingId);
    log.debug("userEntities " + meetingDto.getParticipants());
    String id = meetingDto.getStartAt().format(FORMATTER);
    int dayOfMonth = meetingDto.getStartAt().getDayOfMonth();
    CalendarMonth month = findById(id);
    CalendarDay day = month.getCalendarDay(dayOfMonth);
    if (day != null) {
      day.getMeetings().add(meetingDto);
      Collections.sort(day.getMeetings());
      boolean isRemoved =
          day.getSlots().removeIf(slotEntity -> slotEntity.getId().equals(meetingDto.getId()));
      if (!isRemoved)
        log.warn(
            "Slot with id {} is not found, therefore not removed", meetingDto.getId().toString());
      Collections.sort(day.getSlots());
    } else {
      // TODO maybe throw an exception?!
      log.error("DAY IS NOT FOUND...");
    }
    calendarRepository.save(month);
    log.debug("successfully pushed meeting");
    // TODO delete later
    log.debug("try to get value");
    Optional<CalendarMonth> byId = calendarRepository.findById(id);
    log.debug("is value exists: " + byId.isPresent());
    byId.ifPresent(
        v -> {
          log.debug("the value is: " + v);
        });
  }

  public void removeMeetingFromCalendar(UUID meetingId) {
    MeetingDto meetingDto = meetingService.readMeeting(meetingId);
    String id = meetingDto.getStartAt().format(FORMATTER);
    int dayOfMonth = meetingDto.getStartAt().getDayOfMonth();
    CalendarMonth month = findById(id);
    CalendarDay day = month.getCalendarDay(dayOfMonth);
    boolean isRemoved = day.getMeetings().removeIf(m -> m.getId().equals(meetingDto.getId()));
    if (!isRemoved) {
      log.warn(
          "Meeting with id {} is not found, therefore not removed", meetingDto.getId().toString());
      return;
    }
    Collections.sort(day.getMeetings());
    SlotDto slotDto =
        new SlotDto(meetingDto.getId(), meetingDto.getStartAt(), meetingDto.getEndAt());
    day.getSlots().add(slotDto);
    Collections.sort(day.getSlots());

    calendarRepository.save(month);
    // TODO delete later
    log.debug("removeMeetingFromCalendar: try to get value:");
    Optional<CalendarMonth> byId = calendarRepository.findById(id);
    log.debug("is value exists: " + byId.isPresent());
    byId.ifPresent(
        v -> {
          log.debug("the value is: " + v);
        });
  }

  public CalendarMonthDto readCalendarMonth(String month) {
    YearMonth yearMonth = YearMonth.parse(month, FORMATTER);
    log.debug("parsed local Date value" + yearMonth.toString());
    YearMonth previousMonthDate = yearMonth.minusMonths(1);
    YearMonth nextMonthsDate = yearMonth.plusMonths(1);

    CalendarMonth byId = findById(month);
    CalendarMonth previousMonth = findById(previousMonthDate.format(FORMATTER));
    CalendarMonth nextMonth = findById(nextMonthsDate.format(FORMATTER));
    return convertToDto(byId, previousMonth, nextMonth);
  }

  private CalendarMonth findById(String id) {
    Optional<CalendarMonth> optionalCalendarMonth = calendarRepository.findById(id);
    return optionalCalendarMonth.orElse(CalendarMonth.builder().id(id).build());
  }

  private CalendarMonthDto convertToDto(
      CalendarMonth month, CalendarMonth previousMonth, CalendarMonth nextMonth) {
    CalendarMonthDto dto = modelMapper.map(month, CalendarMonthDto.class);
    dto.setSlotsBefore(getSlotsSum(previousMonth));
    dto.setMeetingsBefore(getMeetingsSum(previousMonth));
    dto.setSlotsAfter(getSlotsSum(nextMonth));
    dto.setMeetingsAfter(getMeetingsSum(nextMonth));

    return dto;
  }

  private Long getSlotsSum(CalendarMonth month) {
    return month.getDays().values().stream()
        .mapToLong(f -> f.getSlots() != null ? f.getSlots().size() : 0L)
        .sum();
  }

  private Long getMeetingsSum(CalendarMonth month) {
    return month.getDays().values().stream()
        .mapToLong(f -> f.getMeetings() != null ? f.getMeetings().size() : 0L)
        .sum();
  }
}
