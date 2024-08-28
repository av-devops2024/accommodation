package com.devops.accommodation.repository;

import ftn.devops.db.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByAccommodation_IdAndDeletedFalseOrderByStartDateAsc(long accommodationId);

    List<Price> findByAccommodation_IdAndDeletedFalseAndEndDateGreaterThanOrderByStartDateAsc(Long accommodationId, LocalDateTime endDate);

    Optional<Price> findByAccommodation_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(long accommodationId, LocalDateTime startDate, LocalDateTime endDate);
}
