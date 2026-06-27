package com.habitflow.repository;

import com.habitflow.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    Optional<HabitLog> findByHabitIdAndLogDate(Long habitId, LocalDate logDate);

    boolean existsByHabitIdAndLogDate(Long habitId, LocalDate logDate);

    List<HabitLog> findByHabitIdAndLogDateBetweenOrderByLogDateAsc(
            Long habitId, LocalDate from, LocalDate to);

    long countByHabitIdAndLogDateBetween(Long habitId, LocalDate from, LocalDate to);

    @Query("SELECT COUNT(DISTINCT l.habitId) FROM HabitLog l WHERE l.logDate = :date")
    long countDistinctHabitsByDate(@Param("date") LocalDate date);

    void deleteByHabitIdAndLogDate(Long habitId, LocalDate logDate);

    List<HabitLog> findByLogDateBetween(LocalDate from, LocalDate to);
}
