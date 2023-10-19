package com.sparta.team2project.schedules.repository;

import com.sparta.team2project.schedules.entity.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SchedulesRepository extends JpaRepository<Schedules, Long> {

}
