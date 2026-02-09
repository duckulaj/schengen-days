package com.hawkins.schengen.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RememberMeIntegrationTest {

    @Autowired
    PersistentTokenRepository repo;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void createsAndUpdatesTokenInH2() {
        String username = "testuser";
        String series = UUID.randomUUID().toString();
        String tokenValue = "tok123";
        Date now = Date.from(Instant.now());

        PersistentRememberMeToken token = new PersistentRememberMeToken(username, series, tokenValue, now);
        repo.createNewToken(token);

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM persistent_logins WHERE series = ?", Integer.class, series);
        assertNotNull(count);
        assertEquals(1, count.intValue());

        // Update token value and last_used
        String newToken = "tok456";
        repo.updateToken(series, newToken, Date.from(Instant.now().plusSeconds(60)));

        String dbToken = jdbc.queryForObject("SELECT token FROM persistent_logins WHERE series = ?", String.class, series);
        assertEquals(newToken, dbToken);

        // Cleanup
        jdbc.update("DELETE FROM persistent_logins WHERE series = ?", series);
    }
}