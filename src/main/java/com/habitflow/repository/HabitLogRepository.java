package com.habitflow.repository;

import com.habitflow.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    boolean existsByHabitIdAndLogDate(Long habitId, LocalDate logDate);

    long countByHabitIdAndLogDateBetween(Long habitId, LocalDate from, LocalDate to);

    @Query("SELECT COUNT(DISTINCT l.habitId) FROM HabitLog l WHERE l.logDate = :date")
    long countDistinctHabitsByDate(@Param("date") LocalDate date);

    @Modifying
    @Transactional
    void deleteByHabitIdAndLogDate(Long habitId, LocalDate logDate);

    List<HabitLog> findByLogDateBetween(LocalDate from, LocalDate to);
}
