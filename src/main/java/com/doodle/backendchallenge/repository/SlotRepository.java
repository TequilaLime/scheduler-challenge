package com.doodle.backendchallenge.repository;

import com.doodle.backendchallenge.entity.SlotEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SlotRepository
    extends JpaRepository<SlotEntity, UUID>, JpaSpecificationExecutor<SlotEntity> {}
