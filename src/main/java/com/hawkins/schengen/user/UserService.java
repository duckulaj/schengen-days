package com.hawkins.schengen.user;

import com.hawkins.schengen.web.dto.RegisterRequest;
import org.eclipse.jdt.annotation.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public void register(RegisterRequest req) {
        createUser(req.username(), req.email(), req.password(), "USER");
    }

    @Transactional
    public void createUser(String username, String email, String password, String role) {
        username = username.trim();
        email = email.trim();

        if (!username.matches("^[A-Za-z0-9._-]{3,50}$")) {
            throw new IllegalArgumentException("Username must be 3-50 chars: letters, numbers, . _ -");
        }
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken.");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use.");
        }
        validateRole(role);
        PasswordPolicy.check(password);

        UserEntity user = new UserEntity(username, encoder.encode(password), role);
        user.setEmail(email);
        repo.save(user);
    }

    @Transactional
    public void updateUser(@NonNull Long id, String email, String role, boolean enabled, String password) {
        UserEntity user = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        email = email.trim();
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (repo.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("Email already in use.");
        }

        user.setEmail(email);
        validateRole(role);
        user.setRole(role);
        user.setEnabled(enabled);

        if (password != null && !password.isBlank()) {
            PasswordPolicy.check(password);
            user.setPasswordHash(encoder.encode(password));
        }

        repo.save(user);
    }

    @Transactional
    public void deleteUser(@NonNull Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        repo.deleteById(id);
    }

    private static void validateRole(String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new IllegalArgumentException("Invalid role.");
        }
    }
}
