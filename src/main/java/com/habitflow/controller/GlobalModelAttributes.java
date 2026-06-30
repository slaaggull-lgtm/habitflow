package com.habitflow.controller;

import com.habitflow.model.User;
import com.habitflow.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired private UserRepository userRepository;

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        Object id = session.getAttribute("activeUserId");
        if (id == null) return null;
        return userRepository.findById((Long) id).orElse(null);
    }
}
