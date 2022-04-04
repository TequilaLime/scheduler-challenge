package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.model.dto.CalendarMonthDto;
import com.doodle.backendchallenge.service.CalendarService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendars")
public class CalendarController {

  private final CalendarService calendarService;

  public CalendarController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @GetMapping
  @ResponseBody
  public CalendarMonthDto readCalendarMonth(@RequestParam String month) {
    return calendarService.readCalendarMonth(month);
  }
}
