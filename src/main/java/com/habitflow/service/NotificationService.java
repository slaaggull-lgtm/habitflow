package com.habitflow.service;

import com.habitflow.model.Badge;
import com.habitflow.model.Habit;
import com.habitflow.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the bell-dropdown notification feed shown in the navbar:
 *  - Achievement notices for badges earned in the last 3 days.
 *  - Daily reminders for habits not yet checked off today.
 *
 * All wording, sorting, and time-formatting logic lives here so the
 * templates stay purely declarative (th:each / th:text).
 */
@Service
public class NotificationService {

    private static final int MAX_NOTIFICATIONS = 8;
    private static final int BADGE_FRESHNESS_DAYS = 3;

    @Autowired private HabitService habitService;
    @Autowired private BadgeRepository badgeRepo;

    public List<NotificationItem> buildNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Badge badge : badgeRepo.findAllByOrderByEarnedDateDesc()) {
            if (badge.getEarnedDate() == null) continue;
            if (badge.getEarnedDate().isBefore(today.minusDays(BADGE_FRESHNESS_DAYS))) continue;

            notifications.add(new NotificationItem(
                    "BADGE",
                    badge.getType().getEmoji(),
                    "Yeni rozet / New badge",
                    badge.getType().getName() + " — " + badge.getType().getDescription(),
                    formatRelativeDate(badge.getEarnedDate())
            ));
        }

        for (Habit habit : habitService.getAllHabits()) {
            if (habitService.isCompletedToday(habit.getId())) continue;

            notifications.add(new NotificationItem(
                    "REMINDER",
                    habit.getCategory().getEmoji(),
                    "Hatırlatma / Reminder",
                    "Bugün \"" + habit.getName() + "\" alışkanlığını unutma! Don't forget it today.",
                    "Today"
            ));
        }

        if (notifications.size() > MAX_NOTIFICATIONS) {
            return notifications.subList(0, MAX_NOTIFICATIONS);
        }
        return notifications;
    }

    public int countActiveReminders() {
        int count = 0;
        for (Habit habit : habitService.getAllHabits()) {
            if (!habitService.isCompletedToday(habit.getId())) count++;
        }
        return count;
    }

    private String formatRelativeDate(LocalDate date) {
        long days = ChronoUnit.DAYS.between(date, LocalDate.now());
        if (days <= 0) return "Today";
        if (days == 1) return "Yesterday";
        return days + " days ago";
    }
}
