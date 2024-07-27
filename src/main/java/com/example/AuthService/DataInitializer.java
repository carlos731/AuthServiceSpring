package com.example.AuthService;

import com.example.AuthService.service.impl.DbInitializerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DbInitializerServiceImpl dbService;

    @Override
    public void run(String... args) throws Exception {
        dbService.initializeDefaultGroupsAndPermissions();
    }
}
