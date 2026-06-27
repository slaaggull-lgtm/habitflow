package com.habitflow.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "habits")
public class Habit {

    public enum Category {
        HEALTH("🌿", "Health"),
        FITNESS("💪", "Fitness"),
        LEARNING("📚", "Learning"),
        MINDFULNESS("🍵", "Mindfulness"),
        CREATIVITY("🎨", "Creativity"),
        PRODUCTIVITY("⚡", "Productivity"),
        SOCIAL("🌸", "Social"),
        OTHER("✨", "Other");

        private final String emoji;
        private final String label;

        Category(String emoji, String label) {
            this.emoji = emoji;
            this.label = label;
        }

        public String getEmoji() { return emoji; }
        public String getLabel() { return label; }
        public String getDisplay() { return emoji + " " + label; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(name = "target_days")
    private int targetDays = 21;

    @Column(name = "created_date")
    private LocalDate createdDate = LocalDate.now();

    private boolean active = true;

    @Column(name = "current_streak")
    private int currentStreak = 0;

    @Column(name = "longest_streak")
    private int longestStreak = 0;

    @Column(name = "total_completions")
    private int totalCompletions = 0;

    private String color = "#7BA05B";

    // Constructors
    public Habit() {}

    public Habit(String name, String description, Category category, int targetDays, String color) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.targetDays = targetDays;
        this.color = color;
        this.createdDate = LocalDate.now();
    }

    // Computed
    public double getProgressPercent() {
        if (targetDays == 0) return 0;
        return Math.min(100.0, (totalCompletions / (double) targetDays) * 100);
    }

    public String getProgressPercentFormatted() {
        return String.format("%.0f", getProgressPercent());
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public int getTargetDays() { return targetDays; }
    public void setTargetDays(int targetDays) { this.targetDays = targetDays; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public int getTotalCompletions() { return totalCompletions; }
    public void setTotalCompletions(int totalCompletions) { this.totalCompletions = totalCompletions; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
