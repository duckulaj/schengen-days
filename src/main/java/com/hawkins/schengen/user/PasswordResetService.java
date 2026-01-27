package com.hawkins.schengen.user;

import com.hawkins.schengen.app.AppProperties;
import com.hawkins.schengen.mail.Mailer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final Mailer mailer;
    private final AppProperties props;

    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository users,
            PasswordResetTokenRepository tokens,
            PasswordEncoder encoder,
            Mailer mailer,
            AppProperties props) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.mailer = mailer;
        this.props = props;
    }

    @Transactional
    public void requestReset(String usernameOrEmail, String ipAddress) {
        // Always respond "OK" to callers (avoid user enumeration)
        var userOpt = users.findByUsernameOrEmail(usernameOrEmail.trim(), usernameOrEmail.trim());

        // opportunistic cleanup
        tokens.deleteByExpiresAtBefore(OffsetDateTime.now().minusDays(1));

        if (userOpt.isEmpty()) {
            return;
        }

        var user = userOpt.get();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return;
        }

        String raw = newToken();
        String hash = sha256Base64(raw);
        tokens.save(new PasswordResetTokenEntity(user.getUsername(), hash, OffsetDateTime.now().plusMinutes(30),
                ipAddress));

        String link = props.getBaseUrl() + "/reset-password?token=" + raw;
        mailer.send(user.getEmail(), "Reset your password", "Use this link within 30 minutes:\n" + link);
    }

    @Transactional
    public void reset(String rawToken, String newPassword) {
        passwordPolicyCheck(newPassword);

        String hash = sha256Base64(rawToken);
        PasswordResetTokenEntity t = tokens.findFirstByTokenHashAndUsedFalse(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        if (t.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        UserEntity user = users.findByUsername(t.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        user.setPasswordHash(encoder.encode(newPassword));
        t.setUsed(true);
    }

    private String newToken() {
        byte[] b = new byte[32];
        random.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private static String sha256Base64(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void passwordPolicyCheck(String p) {
        if (p == null || p.length() < 10) {
            throw new IllegalArgumentException("Password must be at least 10 characters.");
        }
        int cats = 0;
        if (p.matches(".*[a-z].*"))
            cats++;
        if (p.matches(".*[A-Z].*"))
            cats++;
        if (p.matches(".*\\d.*"))
            cats++;
        if (p.matches(".*[^A-Za-z0-9].*"))
            cats++;
        if (cats < 3) {
            throw new IllegalArgumentException("Password must include 3 of: lower, upper, digit, symbol.");
        }
    }
}
