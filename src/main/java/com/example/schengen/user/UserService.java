package com.example.schengen.user;

import com.example.schengen.web.dto.RegisterRequest;
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
        String username = req.username().trim();
        String email = req.email().trim();

        if (!username.matches("^[A-Za-z0-9._-]{3,50}$")) {
            throw new IllegalArgumentException("Username must be 3-50 chars: letters, numbers, . _ -");
        }
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken.");
        }
        // Basic email sanity check. For stricter validation use a library.
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }

        UserEntity user = new UserEntity(username, encoder.encode(req.password()), "USER");
        user.setEmail(email);
        repo.save(user);
    }
}
