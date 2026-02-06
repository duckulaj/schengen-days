package com.hawkins.schengen.web.dto;

import java.time.LocalDate;

public record StayRowResponse(
                long id,
                LocalDate entryDate,
                LocalDate exitDate,
                long days) {
}
