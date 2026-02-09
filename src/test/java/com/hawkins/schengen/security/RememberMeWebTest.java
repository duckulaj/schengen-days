package com.hawkins.schengen.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RememberMeWebTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void loginWithRememberMePersistsToken() throws Exception {
        // Arrange: ensure a test user exists in DB
        String username = "webuser";
        String rawPassword = "secret123";
        String hash = passwordEncoder.encode(rawPassword);
        jdbc.update("DELETE FROM users WHERE username = ?", username);
        jdbc.update("INSERT INTO users (username, email, password_hash, enabled, role) VALUES (?,?,?,?,?)",
                username, username + "@example.com", hash, true, "USER");

        // Act: perform form login with remember-me and CSRF
        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", rawPassword)
                        .param("remember-me", "on")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Assert: token stored in persistent_logins
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM persistent_logins WHERE username = ?", Integer.class, username);
        assertNotNull(count);
        assertTrue(count.intValue() >= 1);
    }
}