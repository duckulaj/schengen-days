package com.example.schengen.user;

import com.example.schengen.app.AppProperties;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSeeder {

    @Bean
    ApplicationRunner seedUsers(UserRepository repo, PasswordEncoder encoder, AppProperties props) {
        return args -> {
            if (!props.getSeed().isEnabled()) return;

            AppProperties.Seed.Admin admin = props.getSeed().getAdmin();
            ensureUser(repo, encoder, admin.getUsername(), admin.getPassword(), "ADMIN");

            for (AppProperties.Seed.DemoUser du : props.getSeed().getDemoUsers()) {
                ensureUser(repo, encoder, du.getUsername(), du.getPassword(), "USER");
            }
        };
    }

    private static void ensureUser(UserRepository repo, PasswordEncoder encoder,
                                   String username, String password, String role) {
        if (username == null || username.isBlank()) return;
        if (password == null || password.isBlank()) return;
        if (repo.existsByUsername(username)) return;
        repo.save(new UserEntity(username.trim(), encoder.encode(password), role));
    }
}
