package com.habitflow.service;

import com.habitflow.model.Badge;
import com.habitflow.model.Habit;
import com.habitflow.model.HabitLog;
import com.habitflow.repository.BadgeRepository;
import com.habitflow.repository.HabitLogRepository;
import com.habitflow.repository.HabitRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class HabitService {

    @Autowired private HabitRepository habitRepo;
    @Autowired private HabitLogRepository logRepo;
    @Autowired private BadgeRepository badgeRepo;

    // ── Habits ──────────────────────────────────────────────

    public List<Habit> getAllHabits() {
        return habitRepo.findByActiveTrueOrderByIdAsc();
    }

    public Optional<Habit> getHabit(Long id) {
        return habitRepo.findById(id);
    }

    @Transactional
    public Habit createHabit(String name, String description,
                              Habit.Category category, int targetDays, String color) {
        Habit h = new Habit(name, description, category, targetDays, color);
        return habitRepo.save(h);
    }

    @Transactional
    public Habit updateHabit(Long id, String name, String description,
                              Habit.Category category, int targetDays, String color) {
        Habit h = habitRepo.findById(id).orElseThrow();
        h.setName(name);
        h.setDescription(description);
        h.setCategory(category);
        h.setTargetDays(targetDays);
        h.setColor(color);
        return habitRepo.save(h);
    }

    @Transactional
    public void deleteHabit(Long id) {
        Habit h = habitRepo.findById(id).orElseThrow();
        h.setActive(false);
        habitRepo.save(h);
    }

    // ── Check-ins ────────────────────────────────────────────

    @Transactional
    public boolean toggleToday(Long habitId) {
        LocalDate today = LocalDate.now();
        Habit habit = habitRepo.findById(habitId).orElseThrow();

        if (logRepo.existsByHabitIdAndLogDate(habitId, today)) {
            logRepo.deleteByHabitIdAndLogDate(habitId, today);
            recalculateStreak(habit);
            habitRepo.save(habit);
            return false;
        } else {
            logRepo.save(new HabitLog(habitId, today));
            recalculateStreak(habit);
            checkAndAwardBadges(habit);
            habitRepo.save(habit);
            return true;
        }
    }

    public boolean isCompletedToday(Long habitId) {
        return logRepo.existsByHabitIdAndLogDate(habitId, LocalDate.now());
    }

    public boolean isCompleted(Long habitId, LocalDate date) {
        return logRepo.existsByHabitIdAndLogDate(habitId, date);
    }

    private void recalculateStreak(Habit habit) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate check = today;
        while (logRepo.existsByHabitIdAndLogDate(habit.getId(), check)) {
            streak++;
            check = check.minusDays(1);
        }
        habit.setCurrentStreak(streak);
        if (streak > habit.getLongestStreak()) {
            habit.setLongestStreak(streak);
        }
        long total = logRepo.countByHabitIdAndLogDateBetween(
                habit.getId(), habit.getCreatedDate(), today);
        habit.setTotalCompletions((int) total);
    }

    private void checkAndAwardBadges(Habit habit) {
        int streak = habit.getCurrentStreak();
        int total = habit.getTotalCompletions();

        if (total >= 1)   award(habit, Badge.Type.FIRST_STEP);
        if (streak >= 7)  award(habit, Badge.Type.WEEK_WARRIOR);
        if (streak >= 14) award(habit, Badge.Type.FORTNIGHT_FLOW);
        if (streak >= 30) award(habit, Badge.Type.MONTHLY_MASTER);
        if (total >= 100) award(habit, Badge.Type.CENTURY_CLUB);

        if (habit.getCategory() == Habit.Category.MINDFULNESS && streak >= 30) {
            award(habit, Badge.Type.MATCHA_ZEN);
        }

        long activeCount = habitRepo.countByActiveTrue();
        if (activeCount >= 5) award(habit, Badge.Type.MULTI_HABIT);

        // Perfect week check
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        List<Habit> all = getAllHabits();
        boolean perfectWeek = all.stream().allMatch(h ->
            logRepo.countByHabitIdAndLogDateBetween(h.getId(), monday, monday.plusDays(6)) >= 7
        );
        if (perfectWeek) award(habit, Badge.Type.PERFECT_WEEK);
    }

    private void award(Habit habit, Badge.Type type) {
        if (!badgeRepo.existsByHabitIdAndType(habit.getId(), type)) {
            badgeRepo.save(new Badge(habit.getId(), type));
        }
    }

    // ── Stats ────────────────────────────────────────────────

    public int getTodayCompletedCount() {
        return (int) logRepo.countDistinctHabitsByDate(LocalDate.now());
    }

    public int getLongestStreakOverall() {
        return getAllHabits().stream()
                .mapToInt(Habit::getLongestStreak).max().orElse(0);
    }

    public int getTotalCompletionsOverall() {
        return getAllHabits().stream()
                .mapToInt(Habit::getTotalCompletions).sum();
    }

    public List<Badge> getAllBadges() {
        return badgeRepo.findAllByOrderByEarnedDateDesc();
    }

    public long getBadgeCount() {
        return badgeRepo.count();
    }

    /** Returns list of {dayLabel, count} for last N days */
    public List<Map<String, Object>> getDailyActivity(int days) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = logRepo.countDistinctHabitsByDate(date);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("date", date.toString());
            entry.put("label", date.getMonthValue() + "/" + date.getDayOfMonth());
            entry.put("count", count);
            result.add(entry);
        }
        return result;
    }

    /** Returns list of {day, count} for current week (Mon–Sun) */
    public List<Map<String, Object>> getWeekActivity() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            long count = logRepo.countDistinctHabitsByDate(date);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("day", days[i]);
            entry.put("count", count);
            entry.put("isToday", date.equals(LocalDate.now()));
            result.add(entry);
        }
        return result;
    }

    public double getWeekCompletionRate() {
        List<Habit> habits = getAllHabits();
        if (habits.isEmpty()) return 0;
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        long actual = 0;
        for (Habit h : habits) {
            actual += logRepo.countByHabitIdAndLogDateBetween(h.getId(), monday, monday.plusDays(6));
        }
        long possible = (long) habits.size() * 7;
        return possible == 0 ? 0 : Math.round((double) actual / possible * 100);
    }

    /** Seeds default habits if DB is empty */
    @Transactional
    public void seedIfEmpty() {
        if (habitRepo.count() == 0) {
            createHabit("Drink Water", "8 glasses of water per day",
                    Habit.Category.HEALTH, 30, "#6BAD8A");
            createHabit("Read Books", "Read for 30 minutes daily",
                    Habit.Category.LEARNING, 21, "#8B9D6A");
            createHabit("Morning Exercise", "30 minutes workout or yoga",
                    Habit.Category.FITNESS, 30, "#7BA05B");
            createHabit("Meditate", "10 minutes mindfulness",
                    Habit.Category.MINDFULNESS, 21, "#A8C5A0");
            createHabit("Code Practice", "1 hour of coding",
                    Habit.Category.PRODUCTIVITY, 66, "#5D8A6B");
        }
    }
}
