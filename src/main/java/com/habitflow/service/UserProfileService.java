package com.habitflow.service;

import com.habitflow.model.UserProfile;
import com.habitflow.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manages the (single) user profile row used for the dynamic greeting,
 * the profile page, and the navbar identity badge.
 */
@Service
public class UserProfileService {

    @Autowired private UserProfileRepository repo;

    @Transactional
    public UserProfile getProfile() {
        return repo.findAll().stream()
                .findFirst()
                .orElseGet(this::createDefaultProfile);
    }

    private UserProfile createDefaultProfile() {
        UserProfile profile = new UserProfile();
        return repo.save(profile);
    }

    @Transactional
    public UserProfile updateProfile(String name, Integer age, String bio, String avatar) {
        UserProfile profile = getProfile();

        if (name != null && !name.isBlank()) {
            profile.setName(name.trim());
        }
        if (age != null && age > 0 && age < 130) {
            profile.setAge(age);
        }
        if (bio != null) {
            profile.setBio(bio.trim());
        }
        if (avatar != null && !avatar.isBlank()) {
            profile.setAvatar(avatar);
        }

        return repo.save(profile);
    }
}
