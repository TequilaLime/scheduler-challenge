package com.doodle.backendchallenge.repository;

import com.doodle.backendchallenge.entity.CalendarMonth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends CrudRepository<CalendarMonth, String> {}
