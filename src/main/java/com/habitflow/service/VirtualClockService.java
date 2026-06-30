package com.habitflow.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tracks the app's virtual "today". Normally this mirrors the real calendar date,
 * but it can be advanced by one day when all habits are completed, so a fresh
 * set of daily tasks becomes available without waiting for the real day to change.
 */
@Service
public class VirtualClockService {

    private final AtomicReference<LocalDate> virtualToday = new AtomicReference<>(LocalDate.now());
    private final AtomicReference<LocalDate> lastRealDate = new AtomicReference<>(LocalDate.now());

    public LocalDate today() {
        // Keep the virtual clock in sync with reality once the real date passes it.
        LocalDate real = LocalDate.now();
        if (real.isAfter(lastRealDate.get())) {
            lastRealDate.set(real);
            virtualToday.updateAndGet(v -> v.isBefore(real) ? real : v);
        }
        return virtualToday.get();
    }

    public void advanceDay() {
        virtualToday.updateAndGet(v -> v.plusDays(1));
    }
}
