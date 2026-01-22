package com.example.schengen.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 10, max = 72) String password,
        @NotBlank @Size(min = 3, max = 200) String email
) {}
