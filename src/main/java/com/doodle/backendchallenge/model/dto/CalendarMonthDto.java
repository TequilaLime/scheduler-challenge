package com.doodle.backendchallenge.model.dto;

import java.util.Map;
import java.util.Objects;

public class CalendarMonthDto {
  private Map<Integer, CalendarDayDto> days;
  private long slotsBefore;
  private long slotsAfter;
  private long meetingsBefore;
  private long meetingsAfter;

  public CalendarMonthDto() {}

  public CalendarMonthDto(
      Map<Integer, CalendarDayDto> days,
      long slotsBefore,
      long slotsAfter,
      long meetingsBefore,
      long meetingsAfter) {
    this.days = days;
    this.slotsBefore = slotsBefore;
    this.slotsAfter = slotsAfter;
    this.meetingsBefore = meetingsBefore;
    this.meetingsAfter = meetingsAfter;
  }

  public Map<Integer, CalendarDayDto> getDays() {
    return days;
  }

  public void setDays(Map<Integer, CalendarDayDto> days) {
    this.days = days;
  }

  public long getSlotsBefore() {
    return slotsBefore;
  }

  public void setSlotsBefore(long slotsBefore) {
    this.slotsBefore = slotsBefore;
  }

  public long getSlotsAfter() {
    return slotsAfter;
  }

  public void setSlotsAfter(long slotsAfter) {
    this.slotsAfter = slotsAfter;
  }

  public long getMeetingsBefore() {
    return meetingsBefore;
  }

  public void setMeetingsBefore(long meetingsBefore) {
    this.meetingsBefore = meetingsBefore;
  }

  public long getMeetingsAfter() {
    return meetingsAfter;
  }

  public void setMeetingsAfter(long meetingsAfter) {
    this.meetingsAfter = meetingsAfter;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CalendarMonthDto that = (CalendarMonthDto) o;
    return slotsBefore == that.slotsBefore
        && slotsAfter == that.slotsAfter
        && meetingsBefore == that.meetingsBefore
        && meetingsAfter == that.meetingsAfter
        && days.equals(that.days);
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, slotsBefore, slotsAfter, meetingsBefore, meetingsAfter);
  }

  @Override
  public String toString() {
    return "CalendarMonth{"
        + "days="
        + days
        + ", slotsBefore="
        + slotsBefore
        + ", slotsAfter="
        + slotsAfter
        + ", meetingsBefore="
        + meetingsBefore
        + ", meetingsAfter="
        + meetingsAfter
        + '}';
  }
}
