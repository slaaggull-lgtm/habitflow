package com.habitflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    @Autowired
    private HabitService habitService;

    @Override
    public void run(ApplicationArguments args) {
        habitService.seedIfEmpty();
    }
}
