package com.doodle.backendchallenge.specifications;

import com.doodle.backendchallenge.entity.SlotEntity;
import java.time.OffsetDateTime;
import org.springframework.data.jpa.domain.Specification;

public interface OverlapsSpecifications {

  static Specification<SlotEntity> startAtOverlaps(OffsetDateTime startAt, OffsetDateTime endAt) {
    return (root, cq, cb) -> cb.between(root.get("startAt"), startAt, endAt);
  }

  static Specification<SlotEntity> endAtOverlaps(OffsetDateTime startAt, OffsetDateTime endAt) {
    return (root, cq, cb) -> cb.between(root.get("endAt"), startAt, endAt);
  }

  static Specification<SlotEntity> endAtLess(OffsetDateTime endAt) {
    return (root, cq, cb) -> cb.lessThan(root.get("endAt"), endAt);
  }
}
