/* (C) 2022 */
package com.doodle.backendchallenge.service;

import com.doodle.backendchallenge.entity.CalendarDay;
import com.doodle.backendchallenge.entity.CalendarMonth;
import com.doodle.backendchallenge.model.dto.CalendarMonthDto;
import com.doodle.backendchallenge.model.dto.MeetingDto;
import com.doodle.backendchallenge.model.dto.SlotDto;
import com.doodle.backendchallenge.repository.CalendarRepository;
import java.time.OffsetDateTime;
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
    log.debug("push slot: {} to Redis", slotId.toString());
    SlotDto slotDto = slotService.readSlot(slotId);
    String redisKey = getRedisKey(slotDto.getStartAt()); // get id
    int dayOfMonth = slotDto.getStartAt().getDayOfMonth();
    CalendarMonth month = findById(redisKey);
    CalendarDay day = month.getCalendarDay(dayOfMonth);
    if (day != null) {
      day.getSlots().add(slotDto);
      Collections.sort(day.getSlots());
    } else {
      log.warn(
          "day of month is null, possible it's only just going to be initialized. id={}, dayOfMonth={}",
          redisKey,
          dayOfMonth);
      CalendarDay build = CalendarDay.builder().slots(Collections.singletonList(slotDto)).build();
      month.getDays().put(dayOfMonth, build);
    }
    calendarRepository.save(month);
    log.debug("successfully pushed slot");
  }

  public void pushMeetingIntoCalendar(UUID meetingId) {
    log.debug("push meeting: {} to Redis", meetingId.toString());
    MeetingDto meetingDto = meetingService.readMeeting(meetingId);
    String redisKey = getRedisKey(meetingDto.getStartAt());
    int dayOfMonth = meetingDto.getStartAt().getDayOfMonth();
    CalendarMonth month = findById(redisKey);
    CalendarDay day = month.getCalendarDay(dayOfMonth);
    if (day == null) { // log warn just in case
      log.warn(
          "Day: {} is not found for redis key: {}, is there any slots for that day.",
          dayOfMonth,
          redisKey);
      return;
    }
    day.getMeetings().add(meetingDto);
    Collections.sort(day.getMeetings());
    boolean isRemoved =
        day.getSlots().removeIf(slotEntity -> slotEntity.getId().equals(meetingDto.getId()));
    if (!isRemoved) // log warn just in case
    {
      log.warn(
          "Slot with id {} is not found, therefore not removed", meetingDto.getId().toString());
    }
    Collections.sort(day.getSlots());

    calendarRepository.save(month);
    log.debug("successfully pushed meeting");
  }

  public void removeMeetingFromCalendar(UUID meetingId) {
    log.debug("remove meeting: {} from redis", meetingId.toString());
    MeetingDto meetingDto = meetingService.readMeeting(meetingId);
    String redisKey = getRedisKey(meetingDto.getStartAt());
    int dayOfMonth = meetingDto.getStartAt().getDayOfMonth();
    CalendarMonth month = findById(redisKey);
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
    log.debug("add slot: {} back to as if it's available", slotDto.getId().toString());
    day.getSlots().add(slotDto);
    Collections.sort(day.getSlots());

    calendarRepository.save(month);
    log.debug("successfully removed meeting");
  }

  public CalendarMonthDto readCalendarMonth(String month) {
    YearMonth yearMonth = YearMonth.parse(month, FORMATTER);
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

  public String getRedisKey(OffsetDateTime dateTime) {
    return dateTime.format(FORMATTER);
  }
}
