package com.devops.accommodation.service.interfaces;

import com.devops.accommodation.dto.response.HostFutureReservationResponse;
import ftn.devops.db.Reservation;
import ftn.devops.db.User;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.HostReservationDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IReservationService extends IReservationRequestService {
    boolean hasApprovedReservationInside(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate);
    boolean hasApprovedReservationIntersect(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate);
    Reservation findById(long reservationRequestId);
    List<Reservation> getIntersectedReservations(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate);
    List<GuestReservationDTO> getReservationsForGuest(User user);
    List<HostReservationDTO> getReservationsForHost(User user, int accommodationId);
    List<GuestReservationDTO> cancelReservation(User user, long reservationRepository);
    HostFutureReservationResponse getFutureReservationsForHost(User user, Long accommodationId);

    boolean hadReservationInAccommodation(User user, Long accommodationId);

    boolean hadReservationFromHost(User user, Long userId);
}
