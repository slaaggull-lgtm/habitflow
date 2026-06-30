package com.habitflow.controller;

import com.habitflow.model.Badge;
import com.habitflow.model.Habit;
import com.habitflow.service.HabitService;
import com.habitflow.service.LevelService;
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

    @Autowired private HabitService service;
    @Autowired private LevelService levelService;
    @Autowired private com.habitflow.repository.UserRepository userRepository;

    @GetMapping("/profile")
    public String profile(Model model, jakarta.servlet.http.HttpSession session) {
        List<Habit> habits = service.getAllHabits();
        List<Badge> badges = service.getUniqueBadges();

        com.habitflow.model.User activeUser = null;
        Object activeId = session.getAttribute("activeUserId");
        if (activeId != null) {
            activeUser = userRepository.findById((Long) activeId).orElse(null);
        }

        int totalCheckins = service.getTotalCompletionsOverall();
        LevelService.LevelInfo level = levelService.getLevelInfo(totalCheckins);

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
        categoryStats.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));

        List<Badge> recentBadges = badges.stream().limit(6).collect(Collectors.toList());

        String memberSince = habits.stream()
                .map(Habit::getCreatedDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .map(d -> d.format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                .orElse(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        model.addAttribute("displayName", activeUser != null ? activeUser.getName() : "No active user");
        model.addAttribute("avatar", activeUser != null ? activeUser.getAvatar() : "👤");
        model.addAttribute("memberSince", memberSince);
        model.addAttribute("level", level.level());
        model.addAttribute("levelLabel", level.label());
        model.addAttribute("levelProgress", level.progressPercent());
        model.addAttribute("nextLevelAt", level.nextThreshold());
        model.addAttribute("totalHabits", habits.size());
        model.addAttribute("totalCheckins", totalCheckins);
        model.addAttribute("bestStreak", service.getLongestStreakOverall());
        model.addAttribute("todayDone", service.getTodayCompletedCount());
        model.addAttribute("badgeCount", badges.size());
        model.addAttribute("totalBadgeCount", Badge.Type.values().length);
        model.addAttribute("recentBadges", recentBadges);
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("avatarOptions", List.of(
                "🌱", "🌿", "🍵", "🌸", "🍃", "🌲", "🦋", "🐝",
                "🌻", "🌙", "⭐", "🎯", "🧘", "🏃", "📚", "🎨"
        ));
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

    private String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}
