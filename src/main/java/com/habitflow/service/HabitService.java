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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class HabitService {

    @Autowired private HabitRepository habitRepo;
    @Autowired private HabitLogRepository logRepo;
    @Autowired private BadgeRepository badgeRepo;
    @Autowired private VirtualClockService clock;

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

    @Transactional
    public boolean toggleToday(Long habitId) {
        LocalDate today = clock.today();
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
            advanceDayIfAllComplete();
            return true;
        }
    }

    /** When every active habit is checked off for the day, move the virtual clock
     *  forward so a fresh day of tasks is immediately available. */
    private void advanceDayIfAllComplete() {
        List<Habit> habits = getAllHabits();
        if (habits.isEmpty()) return;
        LocalDate today = clock.today();
        boolean allDone = habits.stream()
                .allMatch(h -> logRepo.existsByHabitIdAndLogDate(h.getId(), today));
        if (allDone) {
            clock.advanceDay();
        }
    }

    public boolean isCompletedToday(Long habitId) {
        return logRepo.existsByHabitIdAndLogDate(habitId, clock.today());
    }

    private void recalculateStreak(Habit habit) {
        LocalDate today = clock.today();
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

        if (habitRepo.countByActiveTrue() >= 5) {
            awardGlobal(Badge.Type.MULTI_HABIT);
        }

        LocalDate monday = clock.today().with(DayOfWeek.MONDAY);
        List<Habit> all = getAllHabits();
        if (!all.isEmpty()) {
            boolean perfectWeek = all.stream().allMatch(h ->
                logRepo.countByHabitIdAndLogDateBetween(h.getId(), monday, monday.plusDays(6)) >= 7
            );
            if (perfectWeek) awardGlobal(Badge.Type.PERFECT_WEEK);
        }
    }

    private void award(Habit habit, Badge.Type type) {
        if (Badge.isGlobalType(type)) {
            awardGlobal(type);
            return;
        }
        if (!badgeRepo.existsByHabitIdAndType(habit.getId(), type)) {
            badgeRepo.save(new Badge(habit.getId(), type));
        }
    }

    private void awardGlobal(Badge.Type type) {
        if (!badgeRepo.existsByType(type)) {
            badgeRepo.save(new Badge(null, type));
        }
    }

    /** Remove duplicate badge records, keeping the earliest earned per type. */
    @Transactional
    public void cleanupDuplicateBadges() {
        for (Badge.Type type : Badge.Type.values()) {
            List<Badge> dupes = badgeRepo.findByType(type);
            if (dupes.size() <= 1) continue;
            dupes.sort(Comparator.comparing(Badge::getEarnedDate));
            for (int i = 1; i < dupes.size(); i++) {
                badgeRepo.delete(dupes.get(i));
            }
        }
    }

    public int getTodayCompletedCount() {
        return (int) logRepo.countDistinctHabitsByDate(clock.today());
    }

    public int getLongestStreakOverall() {
        return getAllHabits().stream()
                .mapToInt(Habit::getLongestStreak).max().orElse(0);
    }

    public int getTotalCompletionsOverall() {
        return getAllHabits().stream()
                .mapToInt(Habit::getTotalCompletions).sum();
    }

    /** One badge per type for display (most recently earned). */
    public List<Badge> getUniqueBadges() {
        List<Badge> all = badgeRepo.findAllByOrderByEarnedDateDesc();
        Map<Badge.Type, Badge> unique = new LinkedHashMap<>();
        for (Badge b : all) {
            unique.putIfAbsent(b.getType(), b);
        }
        return new ArrayList<>(unique.values());
    }

    public long getBadgeCount() {
        return getUniqueBadges().size();
    }

    public List<Map<String, Object>> getDailyActivity(int days) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = clock.today();
        int maxCount = 1;
        List<Long> counts = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = logRepo.countDistinctHabitsByDate(date);
            counts.add(count);
            if (count > maxCount) maxCount = (int) count;
        }

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = counts.get(days - 1 - i);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("date", date.toString());
            entry.put("label", date.getMonthValue() + "/" + date.getDayOfMonth());
            entry.put("count", count);
            entry.put("percent", maxCount > 0 ? (int) Math.round((double) count / maxCount * 100) : 0);
            result.add(entry);
        }
        return result;
    }

    public List<Map<String, Object>> getWeekActivity() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate monday = clock.today().with(DayOfWeek.MONDAY);
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int totalHabits = getAllHabits().size();
        long maxCount = 1;

        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            long count = logRepo.countDistinctHabitsByDate(date);
            if (count > maxCount) maxCount = count;
        }

        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            long count = logRepo.countDistinctHabitsByDate(date);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("day", days[i]);
            entry.put("count", count);
            entry.put("isToday", date.equals(clock.today()));
            entry.put("percent", maxCount > 0 ? (int) Math.round((double) count / maxCount * 100) : 0);
            entry.put("completionRate", totalHabits > 0
                    ? (int) Math.round((double) count / totalHabits * 100) : 0);
            result.add(entry);
        }
        return result;
    }

    public double getWeekCompletionRate() {
        List<Habit> habits = getAllHabits();
        if (habits.isEmpty()) return 0;
        LocalDate monday = clock.today().with(DayOfWeek.MONDAY);
        long actual = 0;
        for (Habit h : habits) {
            actual += logRepo.countByHabitIdAndLogDateBetween(h.getId(), monday, monday.plusDays(6));
        }
        long possible = (long) habits.size() * 7;
        return possible == 0 ? 0 : Math.round((double) actual / possible * 100);
    }

    /** Per-habit stats for the statistics page. */
    public List<Map<String, Object>> getHabitBreakdown() {
        List<Habit> habits = getAllHabits();
        LocalDate monday = clock.today().with(DayOfWeek.MONDAY);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Habit h : habits) {
            long weekCompletions = logRepo.countByHabitIdAndLogDateBetween(
                    h.getId(), monday, monday.plusDays(6));
            int weekPercent = (int) Math.round((double) weekCompletions / 7 * 100);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("habit", h);
            row.put("weekCompletions", weekCompletions);
            row.put("weekPercent", weekPercent);
            row.put("goalPercent", (int) Math.round(h.getProgressPercent()));
            result.add(row);
        }
        return result;
    }

    @Transactional
    public void seedIfEmpty() {
        cleanupDuplicateBadges();
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
