package com.example.schengen.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StayUpdateRequest(
        @NotNull LocalDate entryDate,
        @NotNull LocalDate exitDate
) {}
