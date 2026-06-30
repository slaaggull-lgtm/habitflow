package com.habitflow.controller;

import com.habitflow.model.Badge;
import com.habitflow.model.User;
import com.habitflow.repository.UserRepository;
import com.habitflow.service.HabitService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired private UserRepository userRepository;
    @Autowired private HabitService habitService;

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        Object id = session.getAttribute("activeUserId");
        if (id == null) return null;
        return userRepository.findById((Long) id).orElse(null);
    }

    @ModelAttribute("recentBadges")
    public List<Badge> recentBadges() {
        List<Badge> all = habitService.getUniqueBadges();
        return all.size() > 5 ? all.subList(0, 5) : all;
    }

    @ModelAttribute("earnedBadgeCount")
    public long earnedBadgeCount() {
        return habitService.getBadgeCount();
    }
}
