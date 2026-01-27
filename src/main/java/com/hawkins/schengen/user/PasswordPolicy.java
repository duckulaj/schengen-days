package com.hawkins.schengen.user;

public final class PasswordPolicy {
    public static void check(String p) {
        if (p == null || p.length() < 10) {
            throw new IllegalArgumentException("Password must be at least 10 characters.");
        }
        int cats = 0;
        if (p.matches(".*[a-z].*")) cats++;
        if (p.matches(".*[A-Z].*")) cats++;
        if (p.matches(".*\\d.*")) cats++;
        if (p.matches(".*[^A-Za-z0-9].*")) cats++;
        if (cats < 3) {
            throw new IllegalArgumentException("Password must include 3 of: lower, upper, digit, symbol.");
        }
    }

    private PasswordPolicy() {}
}
