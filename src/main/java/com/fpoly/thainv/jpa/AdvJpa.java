package com.fpoly.thainv.jpa;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fpoly.thainv.entities.Advertisements;


public interface AdvJpa extends JpaRepository<Advertisements, String> {
 @Query("SELECT adv FROM Advertisements adv WHERE (adv.startDate <= :endDate AND adv.endDate >= :startDate)")
    List<Advertisements> findConflictingAdvertisements(LocalDate startDate, LocalDate endDate);

    Advertisements findByStartDate(LocalDate startDate);  
}
