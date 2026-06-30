package com.habitflow.controller;

import com.habitflow.service.NotificationItem;
import com.habitflow.service.NotificationService;
import com.habitflow.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Supplies model attributes shared by every page through the layout
 * fragment: the notification bell feed/badge and the navbar identity
 * (name + avatar). Centralising it here means individual controllers
 * don't need to repeat this wiring, and it keeps the Thymeleaf side a
 * thin, logic-free renderer of data prepared in Java.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired private NotificationService notificationService;
    @Autowired private UserProfileService userProfileService;

    @ModelAttribute("notifications")
    public List<NotificationItem> notifications() {
        return notificationService.buildNotifications();
    }

    @ModelAttribute("unreadNotificationCount")
    public int unreadNotificationCount() {
        return notifications().size();
    }

    @ModelAttribute("navUsername")
    public String navUsername() {
        return userProfileService.getProfile().getName();
    }

    @ModelAttribute("navAvatar")
    public String navAvatar() {
        return userProfileService.getProfile().getAvatar();
    }
}
