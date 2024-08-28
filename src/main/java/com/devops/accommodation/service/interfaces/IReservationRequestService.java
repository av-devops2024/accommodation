package com.devops.accommodation.service.interfaces;

import com.devops.accommodation.dto.request.CreateReservationRequest;
import ftn.devops.db.User;
import ftn.devops.dto.request.CreateReservationRequestDTO;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.HostReservationDTO;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public interface IReservationRequestService {
    List<GuestReservationDTO> addReservationRequest(User user, CreateReservationRequest reservationRequest);
    List<GuestReservationDTO> deleteReservationRequest(User user, long reservationRequestId);
    List<HostReservationDTO> acceptReservationRequest(User user, long reservationRequestId) throws DataFormatException, IOException;

    List<HostReservationDTO> getReservationRequestsForHost(User user) throws DataFormatException, IOException;
    List<GuestReservationDTO> getReservationRequestsForGuest(User user);
}
