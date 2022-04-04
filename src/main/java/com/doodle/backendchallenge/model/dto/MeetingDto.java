package com.doodle.backendchallenge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingDto {
  private UUID id;
  private UUID slotId;
  private String title;
  private OffsetDateTime startAt;
  private OffsetDateTime endAt;
  private List<UserDto> participants;

  public MeetingDto() {}

  public MeetingDto(
      UUID id,
      String title,
      OffsetDateTime startAt,
      OffsetDateTime endAt,
      List<UserDto> participants) {
    this.id = id;
    this.title = title;
    this.startAt = startAt;
    this.endAt = endAt;
    this.participants = participants;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getSlotId() {
    return slotId;
  }

  public void setSlotId(UUID slotId) {
    this.slotId = slotId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public List<UserDto> getParticipants() {
    return participants;
  }

  public void setParticipants(List<UserDto> participants) {
    this.participants = participants;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MeetingDto meeting = (MeetingDto) o;
    return id.equals(meeting.id)
        && slotId.equals(meeting.slotId)
        && title.equals(meeting.title)
        && startAt.equals(meeting.startAt)
        && endAt.equals(meeting.endAt)
        && Objects.equals(participants, meeting.participants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slotId, title, startAt, endAt, participants);
  }

  @Override
  public String toString() {
    return "Meeting{"
        + "id="
        + id
        + ", slotId="
        + slotId
        + ", title='"
        + title
        + '\''
        + ", startAt="
        + startAt
        + ", endAt="
        + endAt
        + ", participants="
        + participants
        + '}';
  }
}
