package com.example.schengen;

import com.example.schengen.app.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SchengenApp {
    public static void main(String[] args) {
        SpringApplication.run(SchengenApp.class, args);
    }
}
