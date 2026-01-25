package com.example.schengen.stay;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface StayRepository extends JpaRepository<StayEntity, Long> {

        Page<StayEntity> findByUserKeyOrderByEntryDateAsc(String userKey, Pageable pageable);

        List<StayEntity> findByUserKey(String userKey);

        boolean existsByUserKeyAndEntryDateLessThanEqualAndExitDateGreaterThanEqual(
                        String userKey, LocalDate exitDate, LocalDate entryDate);

        @Query("""
                            select (count(s) > 0) from StayEntity s
                            where s.userKey = :userKey
                              and s.id <> :id
                              and s.entryDate <= :exitDate
                              and s.exitDate >= :entryDate
                        """)
        boolean existsOverlappingExceptId(
                        @Param("userKey") String userKey,
                        @Param("id") long id,
                        @Param("entryDate") LocalDate entryDate,
                        @Param("exitDate") LocalDate exitDate);
}
