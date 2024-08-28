package com.devops.accommodation.controller;

import com.devops.accommodation.dto.response.HostFutureReservationResponse;
import com.devops.accommodation.service.interfaces.IReservationService;
import com.devops.accommodation.service.interfaces.IUserService;
import ftn.devops.db.User;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.HostReservationDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private IUserService userService;

    @GetMapping("/cancel/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> cancelReservation(@PathVariable long reservationId,
                                                       HttpServletRequest request){
        User user = userService.getUser(request);
        return reservationService.cancelReservation(user, reservationId);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> getReservationsForGuest(HttpServletRequest request){
        User user = userService.getUser(request);
        return reservationService.getReservationsForGuest(user);
    }

    @GetMapping("host")
    @ResponseStatus(HttpStatus.OK)
    public List<HostReservationDTO> getReservationsForHost( HttpServletRequest request) throws DataFormatException, IOException {
        User user = userService.getUser(request);
        return reservationService.getReservationsForHost(user);
    }

    @GetMapping("/futureReservations/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public HostFutureReservationResponse getFutureReservationsForHost(@PathVariable Long accommodationId,
                                                                      HttpServletRequest request){
        User user = userService.getUser(request);
        return reservationService.getFutureReservationsForHost(user, accommodationId);
    }
}
