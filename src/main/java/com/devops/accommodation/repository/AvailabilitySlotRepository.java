package com.devops.accommodation.repository;

import ftn.devops.db.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByAccommodation_IdAndValidOrderByStartDateAsc(Long id, boolean valid);

    List<AvailabilitySlot> findByAccommodation_IdAndValidAndEndDateGreaterThanOrderByStartDateAsc(Long id, boolean valid, LocalDateTime startDate);

    AvailabilitySlot findByAccommodation_IdAndValidAndStartDateAndEndDate(Long id, boolean valid, LocalDateTime startDate, LocalDateTime endDate);

}
