package com.hawkins.schengen.stay;

import com.hawkins.schengen.engine.SchengenEngine;
import com.hawkins.schengen.web.dto.StatusResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StayService {
    private final StayRepository repo;

    public StayService(StayRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public StatusResponse create(String userKey, LocalDate referenceDate, LocalDate entry, LocalDate exit) {
        validateRange(entry, exit);
        validateNoOverlapCreate(userKey, entry, exit);
        repo.save(new StayEntity(userKey, entry, exit));
        return computeStatus(userKey, referenceDate);
    }

    @Transactional
    public StatusResponse update(String userKey, long id, LocalDate referenceDate, LocalDate entry, LocalDate exit) {
        validateRange(entry, exit);
        StayEntity s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Stay not found"));
        if (!s.getUserKey().equals(userKey)) throw new IllegalArgumentException("Not allowed");
        validateNoOverlapUpdate(userKey, id, entry, exit);
        s.setEntryDate(entry);
        s.setExitDate(exit);
        repo.save(s);
        return computeStatus(userKey, referenceDate);
    }

    @Transactional
    public StatusResponse delete(String userKey, long id, LocalDate referenceDate) {
        StayEntity s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Stay not found"));
        if (!s.getUserKey().equals(userKey)) throw new IllegalArgumentException("Not allowed");
        repo.delete(s);
        return computeStatus(userKey, referenceDate);
    }

    @Transactional(readOnly = true)
    public Page<StayEntity> page(String userKey, int page, int size) {
        int safeSize = Math.max(1, Math.min(size, 100));
        Pageable pageable = PageRequest.of(Math.max(0, page), safeSize, Sort.by("entryDate").ascending());
        return repo.findByUserKeyOrderByEntryDateAsc(userKey, pageable);
    }

    @Transactional(readOnly = true)
    public StatusResponse status(String userKey, LocalDate referenceDate) {
        return computeStatus(userKey, referenceDate);
    }

    private StatusResponse computeStatus(String userKey, LocalDate referenceDate) {
        List<SchengenEngine.Stay> stays = repo.findByUserKey(userKey).stream()
                .map(e -> new SchengenEngine.Stay(e.getEntryDate(), e.getExitDate()))
                .toList();

        SchengenEngine.MergedStays merged = SchengenEngine.preprocess(stays);
        long used = SchengenEngine.usedDaysLast180(merged, referenceDate);
        long remaining = Math.max(0, 90 - used);
        LocalDate next = SchengenEngine.nextLegalEntryDate(merged, referenceDate);
        return new StatusResponse(userKey, referenceDate, used, remaining, next);
    }

    private static void validateRange(LocalDate entry, LocalDate exit) {
        if (exit.isBefore(entry)) throw new IllegalArgumentException("exitDate cannot be before entryDate");
    }

    private void validateNoOverlapCreate(String userKey, LocalDate entry, LocalDate exit) {
        if (repo.existsByUserKeyAndEntryDateLessThanEqualAndExitDateGreaterThanEqual(userKey, exit, entry)) {
            throw new IllegalArgumentException("This stay overlaps an existing stay for your account.");
        }
    }

    private void validateNoOverlapUpdate(String userKey, long id, LocalDate entry, LocalDate exit) {
        if (repo.existsOverlappingExceptId(userKey, id, entry, exit)) {
            throw new IllegalArgumentException("This stay overlaps an existing stay for your account.");
        }
    }
}
