package com.hawkins.schengen.web;

import com.hawkins.schengen.stay.StayEntity;
import com.hawkins.schengen.stay.StayService;
import com.hawkins.schengen.web.dto.*;
import jakarta.validation.Valid;
import org.eclipse.jdt.annotation.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class SchengenApiController {

    private final StayService service;

    public SchengenApiController(StayService service) {
        this.service = service;
    }

    @PostMapping("/stays")
    public StatusResponse addStay(Principal principal,
            @RequestParam LocalDate referenceDate,
            @Valid @RequestBody StayCreateRequest req) {
        return service.create(principal.getName(), referenceDate, req.entryDate(), req.exitDate());
    }

    @PutMapping("/stays/{id}")
    public StatusResponse updateStay(Principal principal,
            @PathVariable long id,
            @RequestParam LocalDate referenceDate,
            @Valid @RequestBody StayUpdateRequest req) {
        return service.update(principal.getName(), id, referenceDate, req.entryDate(), req.exitDate());
    }

    @DeleteMapping("/stays/{id}")
    public StatusResponse deleteStay(Principal principal,
            @PathVariable long id,
            @RequestParam LocalDate referenceDate) {
        return service.delete(principal.getName(), id, referenceDate);
    }

    @GetMapping("/stays")
    public PagedStaysResponse<@NonNull StayRowResponse> listStays(Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StayEntity> p = service.page(principal.getName(), page, size);

        var rows = p.getContent().stream()
                .map(s -> new StayRowResponse(s.getId(), s.getEntryDate(), s.getExitDate()))
                .toList();

        return new PagedStaysResponse<>(rows, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @GetMapping("/status")
    public StatusResponse status(Principal principal, @RequestParam LocalDate referenceDate) {
        return service.status(principal.getName(), referenceDate);
    }
}
