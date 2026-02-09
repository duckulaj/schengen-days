package com.hawkins.schengen.engine;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchengenEngineTest {

    @Test
    void nextLegalEntryDate_noStays_todayIsLegal() {
        SchengenEngine.MergedStays merged = SchengenEngine.preprocess(List.of());
        LocalDate today = LocalDate.of(2025, 1, 1);
        assertEquals(today, SchengenEngine.nextLegalEntryDate(merged, today));
        assertTrue(SchengenEngine.remainingDays(merged, today) >= 0);
    }

    @Test
    void nextLegalEntryDate_exactly90Used_stillLegalEntry() {
        // stays covering 90 days ending day before ref
        LocalDate ref = LocalDate.of(2025, 1, 1);
        LocalDate end = ref.minusDays(1);
        LocalDate start = end.minusDays(89);
        SchengenEngine.MergedStays merged = SchengenEngine.preprocess(List.of(new SchengenEngine.Stay(start, end)));
        LocalDate next = SchengenEngine.nextLegalEntryDate(merged, ref);
        assertEquals(ref, next);
        assertTrue(SchengenEngine.usedDaysLast180(merged, next) <= 90);
    }

    @Test
    void nextLegalEntryDate_overLimit_jumpReducesUsedDays() {
        LocalDate ref = LocalDate.of(2025, 6, 30);
        SchengenEngine.Stay a = new SchengenEngine.Stay(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 2, 28));
        SchengenEngine.Stay b = new SchengenEngine.Stay(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 6, 15));
        SchengenEngine.MergedStays merged = SchengenEngine.preprocess(List.of(a, b));

        long usedAtRef = SchengenEngine.usedDaysLast180(merged, ref);
        assertTrue(usedAtRef > 90);

        LocalDate next = SchengenEngine.nextLegalEntryDate(merged, ref);
        assertTrue(next.isAfter(ref) || next.equals(ref));
        long usedAtNext = SchengenEngine.usedDaysLast180(merged, next);
        assertTrue(usedAtNext <= 90);
    }

    @Test
    void remainingDays_neverNegative() {
        LocalDate ref = LocalDate.of(2025, 6, 30);
        SchengenEngine.Stay s = new SchengenEngine.Stay(ref.minusDays(300), ref.minusDays(200));
        SchengenEngine.MergedStays merged = SchengenEngine.preprocess(List.of(s));
        long rem = SchengenEngine.remainingDays(merged, ref);
        assertTrue(rem >= 0);
    }
}