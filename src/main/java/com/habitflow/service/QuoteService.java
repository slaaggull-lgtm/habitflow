package com.habitflow.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuoteService {

    private static final List<String> QUOTES = List.of(
            "Small steps every day lead to big changes over time.",
            "You don't have to be perfect — you just have to show up.",
            "Consistency beats intensity. Keep growing.",
            "Every check-in is a vote for the person you want to become.",
            "Progress, not perfection. One habit at a time.",
            "The best time to start was yesterday. The next best time is now.",
            "Discipline is choosing what you want most over what you want now.",
            "Your future self will thank you for today's effort.",
            "Growth happens slowly, then all at once. Stay patient.",
            "A journey of a thousand miles begins with a single step.",
            "Make each day your masterpiece.",
            "Habits are the compound interest of self-improvement.",
            "Fall seven times, stand up eight.",
            "What you do today can improve all your tomorrows.",
            "The secret of getting ahead is getting started.",
            "Success is the sum of small efforts repeated day in and day out.",
            "Don't watch the clock; do what it does — keep going.",
            "Motivation gets you started. Habit keeps you going.",
            "Be stronger than your excuses.",
            "Today's accomplishments were yesterday's impossibilities.",
            "Start where you are. Use what you have. Do what you can.",
            "Excellence is not an act, but a habit.",
            "The only bad workout is the one that didn't happen.",
            "One percent better every day adds up to something remarkable.",
            "Your habits shape your identity, and your identity shapes your habits.",
            "Rome wasn't built in a day, but they were laying bricks every hour.",
            "The pain of discipline weighs ounces; the pain of regret weighs tons.",
            "You are one habit away from a completely different life.",
            "Keep watering your habits — growth takes time.",
            "Like matcha, the best results come from steady, mindful practice.",
            "Celebrate progress, no matter how small."
    );

    private static final java.util.Random RANDOM = new java.util.Random();

    public String getDailyQuote() {
        int index = RANDOM.nextInt(QUOTES.size());
        return QUOTES.get(index);
    }
}
