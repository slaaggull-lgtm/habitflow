package com.habitflow.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "badges")
public class Badge {

    public enum Type {
        FIRST_STEP("🌱", "First Step", "Complete your very first habit", false),
        WEEK_WARRIOR("🌿", "Week Warrior", "Maintain a 7-day streak", false),
        FORTNIGHT_FLOW("🍃", "Fortnight Flow", "Maintain a 14-day streak", false),
        MONTHLY_MASTER("🌳", "Monthly Master", "Maintain a 30-day streak", false),
        CENTURY_CLUB("🏆", "Century Club", "Complete a habit 100 times", false),
        MULTI_HABIT("🌸", "Multi-Habit", "Track 5 habits simultaneously", true),
        PERFECT_WEEK("⭐", "Perfect Week", "Complete all habits for a full week", true),
        MATCHA_ZEN("🍵", "Matcha Zen", "30 days of mindfulness habits", false),
        EARLY_BIRD("🌅", "Early Bird", "Complete habits 7 days in a row", false),
        COMEBACK_KID("⚡", "Comeback Kid", "Resume a habit after a break", false);

        private final String emoji;
        private final String name;
        private final String description;
        private final boolean global;

        Type(String emoji, String name, String description, boolean global) {
            this.emoji = emoji;
            this.name = name;
            this.description = description;
            this.global = global;
        }

        public String getEmoji() { return emoji; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isGlobal() { return global; }
    }

    private static final Set<Type> GLOBAL_TYPES = Set.of(Type.MULTI_HABIT, Type.PERFECT_WEEK);

    public static boolean isGlobalType(Type type) {
        return GLOBAL_TYPES.contains(type);
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
