/* (C) 2022 */
package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.exceptions.PreconditionFailedException;
import com.doodle.backendchallenge.model.dto.SlotCollectionDto;
import com.doodle.backendchallenge.model.dto.SlotDto;
import com.doodle.backendchallenge.service.CalendarService;
import com.doodle.backendchallenge.service.SlotService;
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
@RequestMapping("/slots")
public class SlotController {

  private final SlotService slotService;
  private final CalendarService calendarService;

  public SlotController(SlotService slotService, CalendarService calendarService) {
    this.slotService = slotService;
    this.calendarService = calendarService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SlotDto createSlot(@RequestBody SlotDto slot) {
    if (slot.getStartAt() == null || slot.getEndAt() == null) {
      log.warn(
          "one or other property is null startAt: {}, endAt: {}",
          slot.getStartAt(),
          slot.getEndAt());
      throw new PreconditionFailedException("startAt or endAt properties not provided");
    }

    SlotDto slot1 = slotService.createSlot(slot);
    log.debug("after create controller: " + slot1);
    calendarService.pushSlotIntoCalendar(slot1.getId());
    return slot1;
  }

  @GetMapping
  @ResponseBody
  public SlotCollectionDto readSlots(
      @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
    return slotService.readSlots(offset, limit);
  }

  @GetMapping("/{id}")
  public @ResponseBody SlotDto readSlot(@PathVariable UUID id) {
    if (id == null) {
      log.warn("UUID object provided is null in request");
      throw new PreconditionFailedException("UUID object provided is null");
    }
    return slotService.readSlot(id);
  }

  @DeleteMapping("/{id}")
  public void deleteSlot(@PathVariable UUID id) {
    slotService.deleteSlot(id);
  }
}
