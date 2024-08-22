package com.devops.accommodation.service.interfaces;

import ftn.devops.db.User;
import ftn.devops.dto.request.CreateReservationRequestDTO;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.HostReservationDTO;

import java.util.List;

public interface IReservationRequestService {
    List<GuestReservationDTO> addReservationRequest(User user, CreateReservationRequestDTO reservationRequest);
    List<GuestReservationDTO> deleteReservationRequest(User user, long reservationRequestId);
    List<HostReservationDTO> acceptReservationRequest(User user, long reservationRequestId);

    List<HostReservationDTO> getReservationRequestsForHost(User user, long accommodationId);
    List<GuestReservationDTO> getReservationRequestsForGuest(User user);
}
