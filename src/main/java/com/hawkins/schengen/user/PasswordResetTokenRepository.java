package com.hawkins.schengen.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findFirstByTokenHashAndUsedFalse(String tokenHash);
    void deleteByExpiresAtBefore(OffsetDateTime cutoff);
}
