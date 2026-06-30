package com.habitflow.service;

/**
 * A single bell-dropdown entry. Built entirely on the Java side and handed
 * to Thymeleaf as plain data — no client-side notification logic.
 *
 * type: "BADGE" or "REMINDER" (used for styling in the template).
 */
public record NotificationItem(
        String type,
        String icon,
        String title,
        String message,
        String timeLabel
) {}
