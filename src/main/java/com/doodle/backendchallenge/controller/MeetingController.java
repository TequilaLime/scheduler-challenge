/* (C) 2022 */
package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.exceptions.PreconditionFailedException;
import com.doodle.backendchallenge.model.dto.MeetingCollectionDto;
import com.doodle.backendchallenge.model.dto.MeetingDto;
import com.doodle.backendchallenge.service.CalendarService;
import com.doodle.backendchallenge.service.MeetingService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/meetings")
public class MeetingController {

  private final MeetingService meetingService;
  private final CalendarService calendarService;

  public MeetingController(MeetingService meetingService, CalendarService calendarService) {
    this.meetingService = meetingService;
    this.calendarService = calendarService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public MeetingDto createMeeting(@RequestBody MeetingDto meetingDto) {
    if (meetingDto.getSlotId() == null) {
      log.warn("slotId is null in meetingDto");
      throw new PreconditionFailedException("slotId property is not provided");
    }
    if (meetingDto.getTitle().isBlank()) {
      log.warn("title is null in meetingDto");
      throw new PreconditionFailedException("title property is not provided");
    }
    MeetingDto meeting = meetingService.createMeeting(meetingDto);
    calendarService.pushMeetingIntoCalendar(meeting.getId());
    return meeting;
  }

  @GetMapping
  @ResponseBody
  public MeetingCollectionDto readMeetings(
      @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
    return meetingService.readMeetings(offset, limit);
  }

  @GetMapping("/{id}")
  @ResponseBody
  public MeetingDto readMeeting(@PathVariable UUID id) {
    if (id == null) {
      log.warn("UUID object provided is null in request");
      throw new PreconditionFailedException("UUID object provided is null");
    }
    return meetingService.readMeeting(id);
  }

  @DeleteMapping("/{id}")
  public void deleteMeeting(@PathVariable UUID id) {
    calendarService.removeMeetingFromCalendar(id);
    meetingService.deleteMeeting(id);
  }
}
