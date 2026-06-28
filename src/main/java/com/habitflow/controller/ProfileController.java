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

        // Level system based on total completions
        int totalCheckins = service.getTotalCompletionsOverall();
        int level = calculateLevel(totalCheckins);
        String levelLabel = getLevelLabel(level);

        // Category stats
        Map<Habit.Category, Long> catCounts = habits.stream()
                .collect(Collectors.groupingBy(Habit::getCategory, Collectors.counting()));
        long maxCount = catCounts.values().stream().mapToLong(v -> v).max().orElse(1);

        List<Map<String, Object>> categoryStats = new ArrayList<>();
        for (Map.Entry<Habit.Category, Long> e : catCounts.entrySet()) {
            Map<String, Object> cs = new LinkedHashMap<>();
            cs.put("emoji", e.getKey().getEmoji());
            cs.put("label", e.getKey().getLabel());
            cs.put("count", e.getValue());
            cs.put("percent", Math.round((double) e.getValue() / maxCount * 100));
            categoryStats.add(cs);
        }

        // Recent badges (max 4)
        List<Badge> recentBadges = badges.stream().limit(4).collect(Collectors.toList());

        // Member since (first habit creation date or today)
        String memberSince = habits.stream()
                .map(Habit::getCreatedDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .map(d -> d.format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                .orElse(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        List<String> avatarOptions = List.of(
            "🌱", "🌿", "🍵", "🌸", "🍃", "🌲", "🦋", "🐝", "🌻", "🌙",
            "⭐", "🔮", "🎯", "🧘", "🏃", "📚", "🎨", "🎵", "🌊", "🦉"
        );

        model.addAttribute("displayName", "HabitFlow User");
        model.addAttribute("avatar", "🌱");
        model.addAttribute("memberSince", memberSince);
        model.addAttribute("level", level);
        model.addAttribute("levelLabel", levelLabel);
        model.addAttribute("totalHabits", habits.size());
        model.addAttribute("totalCheckins", totalCheckins);
        model.addAttribute("bestStreak", service.getLongestStreakOverall());
        model.addAttribute("badgeCount", badges.size());
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

    /** Keep-alive endpoint so Render doesn't spin down */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    private int calculateLevel(int checkins) {
        if (checkins >= 500) return 10;
        if (checkins >= 200) return 9;
        if (checkins >= 100) return 8;
        if (checkins >= 75)  return 7;
        if (checkins >= 50)  return 6;
        if (checkins >= 30)  return 5;
        if (checkins >= 20)  return 4;
        if (checkins >= 10)  return 3;
        if (checkins >= 5)   return 2;
        return 1;
    }

    private String getLevelLabel(int level) {
        return switch (level) {
            case 1 -> "Seedling";
            case 2 -> "Sprout";
            case 3 -> "Sapling";
            case 4 -> "Young Tree";
            case 5 -> "Grove Keeper";
            case 6 -> "Forest Walker";
            case 7 -> "Moss Elder";
            case 8 -> "Ancient Root";
            case 9 -> "Canopy Master";
            case 10 -> "Forest Spirit";
            default -> "Seedling";
        };
    }

    private String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
