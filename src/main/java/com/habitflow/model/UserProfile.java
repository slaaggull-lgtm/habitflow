package com.habitflow.model;

import jakarta.persistence.*;

/**
 * Single-row entity holding the editable user identity shown across the app
 * (greeting, profile page, navbar avatar). Kept intentionally simple —
 * HabitFlow is a personal, single-user tracker.
 */
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name = "Sıla";

    private Integer age = 22;

    @Column(length = 500)
    private String bio = "Growing one habit at a time 🌱";

    private String avatar = "🌱";

    public UserProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
