package com.hawkins.schengen.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseUrl = "http://localhost:8080";
    private final Seed seed = new Seed();
    private final Mail mail = new Mail();

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public Seed getSeed() { return seed; }
    public Mail getMail() { return mail; }

    public static class Seed {
        private boolean enabled = false;
        private final Admin admin = new Admin();
        @NotNull
        private List<DemoUser> demoUsers = new ArrayList<>();

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public Admin getAdmin() { return admin; }
        public List<DemoUser> getDemoUsers() { return demoUsers; }
        public void setDemoUsers(List<DemoUser> demoUsers) { this.demoUsers = demoUsers; }

        public static class Admin {
            @NotBlank
            private String username;
            @NotBlank
            private String password;
            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
            public String getPassword() { return password; }
            public void setPassword(String password) { this.password = password; }
        }

        public static class DemoUser {
            @NotBlank
            private String username;
            @NotBlank
            private String password;
            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
            public String getPassword() { return password; }
            public void setPassword(String password) { this.password = password; }
        }
    }

    public static class Mail {
        private boolean enabled = false;
        @NotBlank
        private String from;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
    }
}