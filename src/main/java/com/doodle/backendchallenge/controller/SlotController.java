package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.model.dto.SlotCollectionDto;
import com.doodle.backendchallenge.model.dto.SlotDto;
import com.doodle.backendchallenge.service.SlotService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/slots")
public class SlotController {

  private final SlotService slotService;

  public SlotController(SlotService slotService) {
    this.slotService = slotService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SlotDto createSlot(@RequestBody SlotDto slot) {
    return slotService.createSlot(slot);
  }

  @GetMapping
  @ResponseBody
  public SlotCollectionDto readSlots() {
    return slotService.readSlots();
  }

  @GetMapping("/{id}")
  public @ResponseBody SlotDto readSlot(@PathVariable UUID id) {
    return slotService.readSlot(id);
  }

  @DeleteMapping("/{id}")
  public void deleteSlot(@PathVariable UUID id) {
    slotService.deleteSlot(id);
  }
}
