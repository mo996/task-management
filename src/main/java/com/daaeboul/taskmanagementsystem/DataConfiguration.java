package com.daaeboul.taskmanagementsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfiguration {

    @Bean
    CommandLineRunner initData() {
        return args -> {

        };
    }


}
