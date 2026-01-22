package com.example.schengen.user;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "password_reset_tokens", indexes = {
        @Index(name = "idx_prt_user", columnList = "username"),
        @Index(name = "idx_prt_expires", columnList = "expires_at")
})
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "token_hash", nullable = false, length = 128)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(nullable = false)
    private boolean used = false;

    protected PasswordResetTokenEntity() {
    }

    public PasswordResetTokenEntity(String username, String tokenHash, OffsetDateTime expiresAt, String ipAddress) {
        this.username = username;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.used = false;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
