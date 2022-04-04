package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.model.dto.MeetingCollectionDto;
import com.doodle.backendchallenge.model.dto.MeetingDto;
import com.doodle.backendchallenge.service.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/meetings")
public class MeetingController {

  private final MeetingService meetingService;

  public MeetingController(MeetingService meetingService) {
    this.meetingService = meetingService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public MeetingDto createMeeting(@RequestBody MeetingDto meetingDto) {
    return meetingService.createMeeting(meetingDto);
  }

  @GetMapping
  @ResponseBody
  public MeetingCollectionDto readMeetings() {
    return meetingService.readMeetings();
  }

  @GetMapping("/{id}")
  @ResponseBody
  public MeetingDto readMeeting(@PathVariable UUID id) {
    return meetingService.readMeeting(id);
  }

  @DeleteMapping("/{id}")
  public void deleteMeeting(@PathVariable UUID id) {
    meetingService.deleteMeeting(id);
  }
}
