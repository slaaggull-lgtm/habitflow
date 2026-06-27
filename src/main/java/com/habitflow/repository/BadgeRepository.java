package com.habitflow.repository;

import com.habitflow.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    boolean existsByHabitIdAndType(Long habitId, Badge.Type type);
    List<Badge> findAllByOrderByEarnedDateDesc();
    long count();
}
