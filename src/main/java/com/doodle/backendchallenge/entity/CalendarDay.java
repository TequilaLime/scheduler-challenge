package com.doodle.backendchallenge.entity;

import com.doodle.backendchallenge.model.dto.MeetingDto;
import com.doodle.backendchallenge.model.dto.SlotDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarDay implements Serializable {

  private static final long serialVersionUID = 2L;

  @Builder.Default private List<SlotDto> slots = new ArrayList<>();
  @Builder.Default private List<MeetingDto> meetings = new ArrayList<>();
}
