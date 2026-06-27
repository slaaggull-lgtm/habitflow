package com.habitflow.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "badges")
public class Badge {

    public enum Type {
        FIRST_STEP("🌱", "First Step", "Complete your very first habit"),
        WEEK_WARRIOR("🌿", "Week Warrior", "Maintain a 7-day streak"),
        FORTNIGHT_FLOW("🍃", "Fortnight Flow", "Maintain a 14-day streak"),
        MONTHLY_MASTER("🌳", "Monthly Master", "Maintain a 30-day streak"),
        CENTURY_CLUB("🏆", "Century Club", "Complete a habit 100 times"),
        MULTI_HABIT("🌸", "Multi-Habit", "Track 5 habits simultaneously"),
        PERFECT_WEEK("⭐", "Perfect Week", "Complete all habits for a full week"),
        MATCHA_ZEN("🍵", "Matcha Zen", "30 days of mindfulness habits"),
        EARLY_BIRD("🌅", "Early Bird", "Complete habits 7 days in a row"),
        COMEBACK_KID("🔥", "Comeback Kid", "Resume a habit after a break");

        private final String emoji;
        private final String name;
        private final String description;

        Type(String emoji, String name, String description) {
            this.emoji = emoji;
            this.name = name;
            this.description = description;
        }

        public String getEmoji() { return emoji; }
        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "habit_id")
    private Long habitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private Type type;

    @Column(name = "earned_date")
    private LocalDate earnedDate = LocalDate.now();

    public Badge() {}

    public Badge(Long habitId, Type type) {
        this.habitId = habitId;
        this.type = type;
        this.earnedDate = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHabitId() { return habitId; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public LocalDate getEarnedDate() { return earnedDate; }
    public void setEarnedDate(LocalDate earnedDate) { this.earnedDate = earnedDate; }
}
