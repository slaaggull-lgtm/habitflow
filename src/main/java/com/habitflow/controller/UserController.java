package com.habitflow.controller;

import com.habitflow.model.User;
import com.habitflow.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    @Autowired private UserRepository userRepository;

    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("activePage", "users");
        return "pages/users";
    }

    @PostMapping("/users/new")
    public String createUser(@RequestParam String name,
                              @RequestParam(required = false, defaultValue = "") String email,
                              @RequestParam(required = false, defaultValue = "🌱") String avatar,
                              HttpSession session,
                              RedirectAttributes redirect) {
        try {
            if (name == null || name.trim().isEmpty()) {
                redirect.addFlashAttribute("errorMessage", "Name cannot be empty.");
                return "redirect:/users";
            }
            User saved = userRepository.save(new User(name.trim(), email.trim(), avatar.trim()));
            if (session.getAttribute("activeUserId") == null) {
                session.setAttribute("activeUserId", saved.getId());
            }
            redirect.addFlashAttribute("successMessage", "\"" + name.trim() + "\" added.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Could not add user.");
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        try {
            userRepository.deleteById(id);
            Object activeId = session.getAttribute("activeUserId");
            if (activeId != null && activeId.equals(id)) {
                session.removeAttribute("activeUserId");
            }
            redirect.addFlashAttribute("successMessage", "User removed.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Could not remove user.");
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/select")
    public String selectUser(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            session.setAttribute("activeUserId", user.getId());
            redirect.addFlashAttribute("successMessage", "Active user: " + user.getName());
        }
        return "redirect:/users";
    }
}

