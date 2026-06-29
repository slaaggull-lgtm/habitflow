package com.habitflow.repository;

import com.habitflow.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByActiveTrueOrderByIdAsc();
    long countByActiveTrue();
}
