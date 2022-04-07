package com.doodle.backendchallenge.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("CalendarMonth")
public class CalendarMonth implements Serializable {

  private static final long serialVersionUID = 2L;

  @Id private String id;
  @Builder.Default private Map<Integer, CalendarDay> days = new HashMap<>();

  public CalendarDay getCalendarDay(Integer dayOfTheMonth) {
    CalendarDay day = this.days.get(dayOfTheMonth);
    if (day == null) return day;
    if (day.getMeetings() == null) day.setMeetings(new ArrayList<>());
    if (day.getSlots() == null) day.setSlots(new ArrayList<>());
    return day;
  }
}
