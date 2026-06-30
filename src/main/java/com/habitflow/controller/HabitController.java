package com.habitflow.controller;

import com.habitflow.model.Badge;
import com.habitflow.model.Habit;
import com.habitflow.service.HabitService;
import com.habitflow.service.LevelService;
import com.habitflow.service.QuoteService;
import com.habitflow.service.VirtualClockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class HabitController {

    @Autowired private HabitService service;
    @Autowired private LevelService levelService;
    @Autowired private QuoteService quoteService;
    @Autowired private VirtualClockService clock;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Habit> habits = service.getAllHabits();
        int hour = java.time.LocalTime.now().getHour();
        String greeting = hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening";

        int todayDone = service.getTodayCompletedCount();
        int totalHabits = habits.size();
        int totalCheckins = service.getTotalCompletionsOverall();
        LevelService.LevelInfo level = levelService.getLevelInfo(totalCheckins);

        Map<Long, Boolean> doneMap = new HashMap<>();
        for (Habit h : habits) doneMap.put(h.getId(), service.isCompletedToday(h.getId()));

        boolean allComplete = totalHabits > 0 && todayDone >= totalHabits;

        model.addAttribute("greeting", greeting);
        model.addAttribute("today", clock.today().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        model.addAttribute("dailyQuote", quoteService.getDailyQuote());
        model.addAttribute("habits", habits);
        model.addAttribute("todayDone", todayDone);
        model.addAttribute("totalHabits", totalHabits);
        model.addAttribute("allComplete", allComplete);
        model.addAttribute("bestStreak", service.getLongestStreakOverall());
        model.addAttribute("totalCheckins", totalCheckins);
        model.addAttribute("badgeCount", service.getBadgeCount());
        model.addAttribute("weekActivity", service.getWeekActivity());
        model.addAttribute("level", level.level());
        model.addAttribute("levelLabel", level.label());
        model.addAttribute("levelProgress", level.progressPercent());
        model.addAttribute("levelXp", level.xp());
        model.addAttribute("nextLevelAt", level.nextThreshold());
        model.addAttribute("doneMap", doneMap);
        model.addAttribute("activePage", "dashboard");
        return "pages/dashboard";
    }

    @GetMapping("/habits")
    public String habits(Model model,
                         @RequestParam(required = false, defaultValue = "All") String filter) {
        List<Habit> all = service.getAllHabits();
        List<Habit> filtered = filter.equals("All") ? all :
                all.stream().filter(h -> h.getCategory().getLabel().equalsIgnoreCase(filter)).toList();

        Map<Long, Boolean> doneMap = new HashMap<>();
        int doneCount = 0;
        for (Habit h : all) {
            boolean done = service.isCompletedToday(h.getId());
            doneMap.put(h.getId(), done);
            if (done) doneCount++;
        }

        model.addAttribute("habits", filtered);
        model.addAttribute("doneMap", doneMap);
        model.addAttribute("doneCount", doneCount);
        model.addAttribute("totalCount", all.size());
        model.addAttribute("categories", Habit.Category.values());
        model.addAttribute("activeFilter", filter);
        model.addAttribute("activePage", "habits");
        return "pages/habits";
    }

    @GetMapping("/habits/new")
    public String newHabitForm(Model model) {
        model.addAttribute("habit", new Habit());
        model.addAttribute("categories", Habit.Category.values());
        model.addAttribute("palette", getPalette());
        model.addAttribute("activePage", "habits");
        model.addAttribute("editMode", false);
        return "pages/habit-form";
    }

    @PostMapping("/habits/new")
    public String createHabit(@RequestParam String name,
                               @RequestParam(required = false, defaultValue = "") String description,
                               @RequestParam String category,
                               @RequestParam(defaultValue = "21") int targetDays,
                               @RequestParam(defaultValue = "#7BA05B") String color,
                               RedirectAttributes redirect) {
        try {
            service.createHabit(name.trim(), description.trim(),
                    Habit.Category.valueOf(category),
                    Math.max(7, Math.min(90, targetDays)),
                    color);
            redirect.addFlashAttribute("successMessage", "Habit \"" + name.trim() + "\" created successfully!");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Could not create habit. Please check your inputs.");
        }
        return "redirect:/habits";
    }

    @GetMapping("/habits/{id}/edit")
    public String editHabitForm(@PathVariable Long id, Model model) {
        Habit habit = service.getHabit(id).orElseThrow();
        model.addAttribute("habit", habit);
        model.addAttribute("categories", Habit.Category.values());
        model.addAttribute("palette", getPalette());
        model.addAttribute("activePage", "habits");
        model.addAttribute("editMode", true);
        return "pages/habit-form";
    }

    @PostMapping("/habits/{id}/edit")
    public String updateHabit(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false, defaultValue = "") String description,
                               @RequestParam String category,
                               @RequestParam(defaultValue = "21") int targetDays,
                               @RequestParam(defaultValue = "#7BA05B") String color,
                               RedirectAttributes redirect) {
        try {
            service.updateHabit(id, name.trim(), description.trim(),
                    Habit.Category.valueOf(category),
                    Math.max(7, Math.min(90, targetDays)),
                    color);
            redirect.addFlashAttribute("successMessage", "Habit updated successfully!");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Could not update habit.");
        }
        return "redirect:/habits";
    }

    @PostMapping("/habits/{id}/delete")
    public String deleteHabit(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            String name = service.getHabit(id).map(Habit::getName).orElse("Habit");
            service.deleteHabit(id);
            redirect.addFlashAttribute("successMessage", "\"" + name + "\" deleted.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Could not delete habit.");
        }
        return "redirect:/habits";
    }

    @PostMapping("/habits/{id}/toggle")
    public String toggle(@PathVariable Long id,
                          @RequestParam(defaultValue = "/") String redirect,
                          RedirectAttributes flash) {
        boolean done = service.toggleToday(id);
        if (done) {
            flash.addFlashAttribute("successMessage", "Great job! Habit marked complete.");
        }
        return "redirect:" + redirect;
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        List<Habit> habits = service.getAllHabits();

        int activeStreaks = 0;
        for (Habit h : habits) {
            if (h.getCurrentStreak() > 0) activeStreaks++;
        }

        List<Map<String, Object>> monthActivity = service.getDailyActivity(30);
        int monthMax = monthActivity.stream()
                .mapToInt(d -> ((Number) d.get("count")).intValue())
                .max().orElse(1);

        model.addAttribute("habits", habits);
        model.addAttribute("habitBreakdown", service.getHabitBreakdown());
        model.addAttribute("totalHabits", habits.size());
        model.addAttribute("activeStreaks", activeStreaks);
        model.addAttribute("weekRate", service.getWeekCompletionRate());
        model.addAttribute("totalCheckins", service.getTotalCompletionsOverall());
        model.addAttribute("bestStreak", service.getLongestStreakOverall());
        model.addAttribute("monthActivity", monthActivity);
        model.addAttribute("monthMax", monthMax);
        model.addAttribute("weekActivity", service.getWeekActivity());
        model.addAttribute("activePage", "statistics");
        return "pages/statistics";
    }

    @GetMapping("/badges")
    public String badges(Model model) {
        List<Badge> earned = service.getUniqueBadges();
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

    private List<String> getPalette() {
        return List.of(
                "#6BAD8A", "#7BA05B", "#8B9D6A", "#A8C5A0", "#5D8A6B",
                "#B5C8A0", "#4A7B5E", "#C4D9BC", "#3D6B4F", "#9AB88A",
                "#6B8EAD", "#8AADAD", "#AD8A6B", "#8A6BAD", "#AD6B8A"
        );
    }
}
