package com.devops.accommodation.repository;

import ftn.devops.db.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    /* reservation request */
    List<Reservation> findByAccommodation_IdAndStartDateAfterAndApprovedFalseAndDeletedFalse(Long id, LocalDateTime startDate);
    List<Reservation> findByGuest_IdAndStartDateAfterAndApprovedFalseAndDeletedFalse(Long id, LocalDateTime startDate);

    /* interval check */
    boolean existsByAccommodation_IdAndStartDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(Long id, LocalDateTime startDateStart, LocalDateTime startDateEnd);
    boolean existsByAccommodation_IdAndEndDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(Long id, LocalDateTime startDateStart, LocalDateTime startDateEnd);
    boolean existsByAccommodation_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndApprovedTrueAndCancelledFalseAndDeletedFalse(Long id, LocalDateTime startDate, LocalDateTime endDate);
    // res contained
    boolean existsByAccommodation_IdAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndCancelledFalseAndApprovedTrueAndDeletedFalse(Long id, LocalDateTime startDate, LocalDateTime endDate);


    /* get user statistics */
    long countByGuest_IdAndDeletedFalseAndApprovedTrue(Long id);
    long countByGuest_IdAndDeletedFalseAndApprovedTrueAndCancelledTrue(Long id);

    /* reservation */
    List<Reservation> findByAccommodation_IdAndStartDateAfterAndApprovedTrueAndDeletedFalseAndCancelledFalse(Long accommodationId, LocalDateTime now);

    List<Reservation> findByGuest_IdAndStartDateAfterAndApprovedTrueAndDeletedFalseAndCancelledFalse(long l, LocalDateTime now);

    /* interval reservations */
    List<Reservation> findByAccommodation_IdAndStartDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(Long id, LocalDateTime startDateStart, LocalDateTime startDateEnd);
    List<Reservation> findByAccommodation_IdAndEndDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(Long id, LocalDateTime startDateStart, LocalDateTime startDateEnd);
    List<Reservation> findByAccommodation_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndApprovedTrueAndCancelledFalseAndDeletedFalse(Long id, LocalDateTime startDate, LocalDateTime endDate);
    List<Reservation> findByAccommodationIdAndCancelledFalseAndDeletedFalseAndApprovedTrueAndStartDateAfter(Long accommodationId, LocalDateTime date);
}
