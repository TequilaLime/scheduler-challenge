package com.doodle.backendchallenge.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "slotEntity")
@Table(name = "slots")
public class SlotEntity implements Serializable {

  private static final long serialVersionUID = 2L;

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  @Builder.Default
  protected UUID id = UUID.randomUUID();

  @Column(name = "title")
  private String title;

  @Column(name = "start_at")
  @NotNull
  private OffsetDateTime startAt;

  @Column(name = "end_at")
  @NotNull
  private OffsetDateTime endAt;
}
