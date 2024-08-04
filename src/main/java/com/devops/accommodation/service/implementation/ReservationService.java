package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.ReservationRepository;
import com.devops.accommodation.service.interfaces.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService implements IReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public boolean hasApprovedReservation(LocalDateTime fromDate, LocalDateTime toDate) {
        return !reservationRepository
                .findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndCancelledFalseAndApprovedTrue(fromDate, toDate)
                .isEmpty();
    }
}
