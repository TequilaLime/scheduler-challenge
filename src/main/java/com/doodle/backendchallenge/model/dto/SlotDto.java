package com.doodle.backendchallenge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SlotDto {
  private UUID id;
  private OffsetDateTime startAt;
  private OffsetDateTime endAt;

  public SlotDto() {}

  public SlotDto(UUID id, OffsetDateTime startAt, OffsetDateTime endAt) {
    this.id = id;
    this.startAt = startAt;
    this.endAt = endAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OffsetDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(OffsetDateTime startAt) {
    this.startAt = startAt;
  }

  public OffsetDateTime getEndAt() {
    return endAt;
  }

  public void setEndAt(OffsetDateTime endAt) {
    this.endAt = endAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SlotDto slot = (SlotDto) o;
    return id.equals(slot.id) && startAt.equals(slot.startAt) && endAt.equals(slot.endAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, startAt, endAt);
  }

  @Override
  public String toString() {
    return "Slot{" + "id=" + id + ", startAt=" + startAt + ", endAt=" + endAt + '}';
  }
}
