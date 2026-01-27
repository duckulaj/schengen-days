package com.hawkins.schengen.web.dto;

import java.time.LocalDate;

public record StatusResponse(
        String userKey,
        LocalDate referenceDate,
        long usedDaysLast180,
        long remainingDays,
        LocalDate nextLegalEntryDate
) {}
