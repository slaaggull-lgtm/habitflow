package com.habitflow.controller;

import com.habitflow.model.Badge;
import com.habitflow.model.Habit;
import com.habitflow.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class HabitController {

    @Autowired
    private HabitService service;

    // ── Dashboard ─────────────────────────────────────────────

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Habit> habits = service.getAllHabits();
        int hour = java.time.LocalTime.now().getHour();
        String greeting = hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening";

        model.addAttribute("greeting", greeting);
        model.addAttribute("today", LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        model.addAttribute("habits", habits);
        model.addAttribute("todayDone", service.getTodayCompletedCount());
        model.addAttribute("totalHabits", habits.size());
        model.addAttribute("bestStreak", service.getLongestStreakOverall());
        model.addAttribute("totalCheckins", service.getTotalCompletionsOverall());
        model.addAttribute("badgeCount", service.getBadgeCount());
        model.addAttribute("weekActivity", service.getWeekActivity());
        model.addAttribute("activePage", "dashboard");

        // Mark which habits are done today
        Map<Long, Boolean> doneMap = new HashMap<>();
        for (Habit h : habits) doneMap.put(h.getId(), service.isCompletedToday(h.getId()));
        model.addAttribute("doneMap", doneMap);

        return "pages/dashboard";
    }

    // ── Habits page ───────────────────────────────────────────

    @GetMapping("/habits")
    public String habits(Model model,
                         @RequestParam(required = false, defaultValue = "All") String filter) {
        List<Habit> all = service.getAllHabits();
        List<Habit> filtered = filter.equals("All") ? all :
                all.stream().filter(h -> h.getCategory().getLabel().equalsIgnoreCase(filter)).toList();

        Map<Long, Boolean> doneMap = new HashMap<>();
        for (Habit h : all) doneMap.put(h.getId(), service.isCompletedToday(h.getId()));

        model.addAttribute("habits", filtered);
        model.addAttribute("doneMap", doneMap);
        model.addAttribute("categories", Habit.Category.values());
        model.addAttribute("activeFilter", filter);
        model.addAttribute("activePage", "habits");
        return "pages/habits";
    }

    // ── Add / Edit habit ──────────────────────────────────────

    @GetMapping("/habits/new")
    public String newHabitForm(Model model) {
        model.addAttribute("habit", new Habit());
        model.addAttribute("categories", Habit.Category.values());
        model.addAttribute("palette", List.of(
                "#6BAD8A","#7BA05B","#8B9D6A","#A8C5A0","#5D8A6B",
                "#B5C8A0","#4A7B5E","#C4D9BC","#3D6B4F","#9AB88A"
        ));
        model.addAttribute("activePage", "habits");
        model.addAttribute("editMode", false);
        return "pages/habit-form";
    }

    @PostMapping("/habits/new")
    public String createHabit(@RequestParam String name,
                               @RequestParam(required = false, defaultValue = "") String description,
                               @RequestParam String category,
                               @RequestParam int targetDays,
                               @RequestParam(defaultValue = "#7BA05B") String color) {
        service.createHabit(name, description,
                Habit.Category.valueOf(category), targetDays, color);
        return "redirect:/habits";
    }

    @GetMapping("/habits/{id}/edit")
    public String editHabitForm(@PathVariable Long id, Model model) {
        Habit habit = service.getHabit(id).orElseThrow();
        model.addAttribute("habit", habit);
        model.addAttribute("categories", Habit.Category.values());
        model.addAttribute("palette", List.of(
                "#6BAD8A","#7BA05B","#8B9D6A","#A8C5A0","#5D8A6B",
                "#B5C8A0","#4A7B5E","#C4D9BC","#3D6B4F","#9AB88A"
        ));
        model.addAttribute("activePage", "habits");
        model.addAttribute("editMode", true);
        return "pages/habit-form";
    }

    @PostMapping("/habits/{id}/edit")
    public String updateHabit(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false, defaultValue = "") String description,
                               @RequestParam String category,
                               @RequestParam int targetDays,
                               @RequestParam(defaultValue = "#7BA05B") String color) {
        service.updateHabit(id, name, description,
                Habit.Category.valueOf(category), targetDays, color);
        return "redirect:/habits";
    }

    @PostMapping("/habits/{id}/delete")
    public String deleteHabit(@PathVariable Long id) {
        service.deleteHabit(id);
        return "redirect:/habits";
    }

    // ── Toggle check-in ───────────────────────────────────────

    @PostMapping("/habits/{id}/toggle")
    public String toggle(@PathVariable Long id,
                          @RequestParam(defaultValue = "/") String redirect) {
        service.toggleToday(id);
        return "redirect:" + redirect;
    }

    // ── Statistics ────────────────────────────────────────────

    @GetMapping("/statistics")
    public String statistics(Model model) {
        List<Habit> habits = service.getAllHabits();
        long activeStreaks = habits.stream().filter(h -> h.getCurrentStreak() > 0).count();

        model.addAttribute("habits", habits);
        model.addAttribute("totalHabits", habits.size());
        model.addAttribute("activeStreaks", activeStreaks);
        model.addAttribute("weekRate", service.getWeekCompletionRate());
        model.addAttribute("totalCheckins", service.getTotalCompletionsOverall());
        model.addAttribute("monthActivity", service.getDailyActivity(30));
        model.addAttribute("activePage", "statistics");
        return "pages/statistics";
    }

    // ── Badges ────────────────────────────────────────────────

    @GetMapping("/badges")
    public String badges(Model model) {
        List<Badge> earned = service.getAllBadges();
        Set<Badge.Type> earnedTypes = new HashSet<>();
        for (Badge b : earned) earnedTypes.add(b.getType());

        List<Badge.Type> locked = Arrays.stream(Badge.Type.values())
                .filter(t -> !earnedTypes.contains(t)).toList();

        model.addAttribute("earnedBadges", earned);
        model.addAttribute("lockedTypes", locked);
        model.addAttribute("earnedCount", earned.size());
        model.addAttribute("totalCount", Badge.Type.values().length);
        model.addAttribute("activePage", "badges");
        return "pages/badges";
    }
}
