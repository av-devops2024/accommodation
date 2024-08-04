package com.devops.accommodation.service.interfaces;

import java.time.LocalDateTime;

public interface IReservationService {
    boolean hasApprovedReservation(LocalDateTime fromDate, LocalDateTime toDate);
}
