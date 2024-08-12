package com.devops.accommodation.repository;

import ftn.devops.db.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByAccommodation_IdAndDeletedFalseOrderByStartDateAsc(long accommodationId);

    List<Price> findByAccommodation_IdAndDeletedFalseAndEndDateGreaterThanOrderByStartDateAsc(long accommodationId, LocalDateTime endDate);
}
