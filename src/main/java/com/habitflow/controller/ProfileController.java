package com.habitflow.controller;

import com.habitflow.model.Badge;
import com.habitflow.model.Habit;
import com.habitflow.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    @Autowired
    private HabitService service;

    @GetMapping("/profile")
    public String profile(Model model) {
        List<Habit> habits = service.getAllHabits();
        List<Badge> badges = service.getAllBadges();

        int totalCheckins = service.getTotalCompletionsOverall();
        int level = calculateLevel(totalCheckins);
        String levelLabel = getLevelLabel(level);
        int nextLevelAt = nextLevelThreshold(level);
        int prevLevelAt = prevLevelThreshold(level);
        int levelProgress = nextLevelAt > prevLevelAt
                ? (int) Math.min(100, (double)(totalCheckins - prevLevelAt) / (nextLevelAt - prevLevelAt) * 100)
                : 100;

        // Category stats — computed in Java, not Thymeleaf
        Map<Habit.Category, Long> catCounts = habits.stream()
                .collect(Collectors.groupingBy(Habit::getCategory, Collectors.counting()));
        long maxCount = catCounts.values().stream().mapToLong(v -> v).max().orElse(1);

        List<Map<String, Object>> categoryStats = new ArrayList<>();
        for (Map.Entry<Habit.Category, Long> e : catCounts.entrySet()) {
            Map<String, Object> cs = new LinkedHashMap<>();
            cs.put("emoji", e.getKey().getEmoji());
            cs.put("label", e.getKey().getLabel());
            cs.put("count", e.getValue());
            cs.put("percent", (int) Math.round((double) e.getValue() / maxCount * 100));
            categoryStats.add(cs);
        }
        categoryStats.sort((a, b) -> (Long) b.get("count") > (Long) a.get("count") ? 1 : -1);

        List<Badge> recentBadges = badges.stream().limit(6).collect(Collectors.toList());

        String memberSince = habits.stream()
                .map(Habit::getCreatedDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .map(d -> d.format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                .orElse(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        int bestStreak = service.getLongestStreakOverall();
        long todayDone = (long) service.getTodayCompletedCount();

        List<String> avatarOptions = List.of(
            "🌱", "🌿", "🍵", "🌸", "🍃", "🌲", "🦋", "🐝",
            "🌻", "🌙", "⭐", "🎯", "🧘", "🏃", "📚", "🎨"
        );

        model.addAttribute("displayName", "HabitFlow User");
        model.addAttribute("avatar", "🌱");
        model.addAttribute("memberSince", memberSince);
        model.addAttribute("level", level);
        model.addAttribute("levelLabel", levelLabel);
        model.addAttribute("levelProgress", levelProgress);
        model.addAttribute("nextLevelAt", nextLevelAt);
        model.addAttribute("totalHabits", habits.size());
        model.addAttribute("totalCheckins", totalCheckins);
        model.addAttribute("bestStreak", bestStreak);
        model.addAttribute("todayDone", todayDone);
        model.addAttribute("badgeCount", badges.size());
        model.addAttribute("totalBadgeCount", Badge.Type.values().length);
        model.addAttribute("recentBadges", recentBadges);
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("avatarOptions", avatarOptions);
        model.addAttribute("activePage", "profile");
        return "pages/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("activePage", "settings");
        return "pages/settings";
    }

    @GetMapping("/export/habits")
    public ResponseEntity<String> exportHabits() {
        List<Habit> habits = service.getAllHabits();
        StringBuilder csv = new StringBuilder();
        csv.append("Name,Category,Target Days,Current Streak,Longest Streak,Total Completions,Created Date\n");
        for (Habit h : habits) {
            csv.append(escape(h.getName())).append(",")
               .append(h.getCategory().getLabel()).append(",")
               .append(h.getTargetDays()).append(",")
               .append(h.getCurrentStreak()).append(",")
               .append(h.getLongestStreak()).append(",")
               .append(h.getTotalCompletions()).append(",")
               .append(h.getCreatedDate()).append("\n");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"habits.csv\"")
                .body(csv.toString());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    private int calculateLevel(int c) {
        if (c >= 500) return 10;
        if (c >= 200) return 9;
        if (c >= 100) return 8;
        if (c >= 75)  return 7;
        if (c >= 50)  return 6;
        if (c >= 30)  return 5;
        if (c >= 20)  return 4;
        if (c >= 10)  return 3;
        if (c >= 5)   return 2;
        return 1;
    }

    private int nextLevelThreshold(int level) {
        return switch (level) {
            case 1 -> 5; case 2 -> 10; case 3 -> 20; case 4 -> 30;
            case 5 -> 50; case 6 -> 75; case 7 -> 100; case 8 -> 200;
            case 9 -> 500; default -> 500;
        };
    }

    private int prevLevelThreshold(int level) {
        return switch (level) {
            case 1 -> 0; case 2 -> 5; case 3 -> 10; case 4 -> 20;
            case 5 -> 30; case 6 -> 50; case 7 -> 75; case 8 -> 100;
            case 9 -> 200; default -> 500;
        };
    }

    private String getLevelLabel(int level) {
        return switch (level) {
            case 1 -> "Seedling"; case 2 -> "Sprout"; case 3 -> "Sapling";
            case 4 -> "Young Tree"; case 5 -> "Grove Keeper"; case 6 -> "Forest Walker";
            case 7 -> "Moss Elder"; case 8 -> "Ancient Root"; case 9 -> "Canopy Master";
            case 10 -> "Forest Spirit"; default -> "Seedling";
        };
    }

    private String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}
