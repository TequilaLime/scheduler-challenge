package com.doodle.backendchallenge.model.dto;

import java.util.List;
import java.util.Objects;

public class CalendarDayDto {
  private List<SlotDto> slots;
  private List<MeetingDto> meetings;

  public CalendarDayDto() {}

  public CalendarDayDto(List<SlotDto> slots, List<MeetingDto> meetings) {
    this.slots = slots;
    this.meetings = meetings;
  }

  public List<SlotDto> getSlots() {
    return slots;
  }

  public void setSlots(List<SlotDto> slots) {
    this.slots = slots;
  }

  public List<MeetingDto> getMeetings() {
    return meetings;
  }

  public void setMeetings(List<MeetingDto> meetings) {
    this.meetings = meetings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CalendarDayDto that = (CalendarDayDto) o;
    return slots.equals(that.slots) && meetings.equals(that.meetings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slots, meetings);
  }

  @Override
  public String toString() {
    return "CalendarDay{" + "slots=" + slots + ", meetings=" + meetings + '}';
  }
}
