package com.habitflow.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LevelService {

    public record LevelInfo(int level, String label, int xp, int prevThreshold, int nextThreshold, int progressPercent) {}

    private static final Map<Integer, String> LABELS = new LinkedHashMap<>();
    static {
        LABELS.put(1, "Seedling");
        LABELS.put(2, "Sprout");
        LABELS.put(3, "Sapling");
        LABELS.put(4, "Young Tree");
        LABELS.put(5, "Grove Keeper");
        LABELS.put(6, "Forest Walker");
        LABELS.put(7, "Moss Elder");
        LABELS.put(8, "Ancient Root");
        LABELS.put(9, "Canopy Master");
        LABELS.put(10, "Forest Spirit");
    }

    public LevelInfo getLevelInfo(int totalCheckins) {
        int level = calculateLevel(totalCheckins);
        int prev = prevLevelThreshold(level);
        int next = nextLevelThreshold(level);
        int progress = next > prev
                ? (int) Math.min(100, (double) (totalCheckins - prev) / (next - prev) * 100)
                : 100;
        return new LevelInfo(level, getLevelLabel(level), totalCheckins, prev, next, progress);
    }

    public int calculateLevel(int checkins) {
        if (checkins >= 500) return 10;
        if (checkins >= 200) return 9;
        if (checkins >= 100) return 8;
        if (checkins >= 75)  return 7;
        if (checkins >= 50)  return 6;
        if (checkins >= 30)  return 5;
        if (checkins >= 20)  return 4;
        if (checkins >= 10)  return 3;
        if (checkins >= 5)   return 2;
        return 1;
    }

    public int nextLevelThreshold(int level) {
        return switch (level) {
            case 1 -> 5; case 2 -> 10; case 3 -> 20; case 4 -> 30;
            case 5 -> 50; case 6 -> 75; case 7 -> 100; case 8 -> 200;
            case 9 -> 500; default -> 500;
        };
    }

    public int prevLevelThreshold(int level) {
        return switch (level) {
            case 1 -> 0; case 2 -> 5; case 3 -> 10; case 4 -> 20;
            case 5 -> 30; case 6 -> 50; case 7 -> 75; case 8 -> 100;
            case 9 -> 200; default -> 500;
        };
    }

    public String getLevelLabel(int level) {
        return LABELS.getOrDefault(level, "Seedling");
    }
}
