package com.devops.accommodation.service.interfaces;

import ftn.devops.db.Price;
import com.devops.accommodation.dto.response.HostFutureReservationResponse;
import ftn.devops.db.Reservation;
import ftn.devops.db.User;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.HostReservationDTO;
import org.antlr.v4.runtime.misc.Pair;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.DataFormatException;

public interface IReservationService extends IReservationRequestService {
    boolean hasApprovedReservationInside(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate);
    boolean hasApprovedReservationIntersect(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate);
    Reservation findById(long reservationRequestId);
    Reservation findByRatingId(Long ratingId);
    List<Reservation> getIntersectedReservations(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate);
    List<GuestReservationDTO> getReservationsForGuest(User user);
    List<Reservation> getRatedReservations(User user);
    List<Reservation> getUnratedReservations(User user);
    List<HostReservationDTO> getReservationsForHost(User user) throws DataFormatException, IOException;
    List<GuestReservationDTO> cancelReservation(User user, long reservationRepository);
    Pair<Price, Double> countPrice(long accommodationId, LocalDateTime startDate, LocalDateTime endDate, int numberOfGuest);
    HostFutureReservationResponse getFutureReservationsForHost(User user, Long accommodationId);
    boolean hadReservationInAccommodation(User user, Long accommodationId);

    boolean hadPastReservationInAccommodation(User user, Long accommodationId);

    boolean hadReservationFromHost(User user, Long userId);
    Reservation save (Reservation reservation);
}
