package com.doodle.backendchallenge.repository;

import com.doodle.backendchallenge.entity.MeetingEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MeetingsRepository
    extends JpaRepository<MeetingEntity, UUID>, JpaSpecificationExecutor<MeetingEntity> {}
