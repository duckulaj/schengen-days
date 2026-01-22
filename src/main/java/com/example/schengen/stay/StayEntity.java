package com.example.schengen.stay;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "stays", indexes = {
        @Index(name = "idx_stays_user_entry", columnList = "user_key, entry_date"),
        @Index(name = "idx_stays_user_exit", columnList = "user_key, exit_date")
})
public class StayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_key", nullable = false, length = 100)
    private String userKey;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "exit_date", nullable = false)
    private LocalDate exitDate;

    protected StayEntity() {}

    public StayEntity(String userKey, LocalDate entryDate, LocalDate exitDate) {
        this.userKey = userKey;
        this.entryDate = entryDate;
        this.exitDate = exitDate;
    }

    public Long getId() { return id; }
    public String getUserKey() { return userKey; }
    public LocalDate getEntryDate() { return entryDate; }
    public LocalDate getExitDate() { return exitDate; }

    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    public void setExitDate(LocalDate exitDate) { this.exitDate = exitDate; }
}
