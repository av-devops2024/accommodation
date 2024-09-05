package com.devops.accommodation.controller;

import com.devops.accommodation.dto.response.HostFutureReservationResponse;
import com.devops.accommodation.service.interfaces.IReservationService;
import com.devops.accommodation.service.interfaces.IUserService;
import ftn.devops.db.User;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.HostReservationDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private IUserService userService;

    @GetMapping("/cancel/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> cancelReservation(@PathVariable long reservationId,
                                                       HttpServletRequest request){
        logger.info("Cancel reservation {} ", reservationId);
        User user = userService.getUser(request);
        return reservationService.cancelReservation(user, reservationId);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> getReservationsForGuest(HttpServletRequest request){
        logger.info("Get reservation - {} ", applicationName);
        User user = userService.getUser(request);
        return reservationService.getReservationsForGuest(user);
    }

    @GetMapping("host")
    @ResponseStatus(HttpStatus.OK)
    public List<HostReservationDTO> getReservationsForHost( HttpServletRequest request) throws DataFormatException, IOException {
        logger.info("Get reservations for host- {} ", applicationName);
        User user = userService.getUser(request);
        return reservationService.getReservationsForHost(user);
    }

    @GetMapping("/futureReservations/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public HostFutureReservationResponse getFutureReservationsForHost(@PathVariable Long accommodationId,
                                                                      HttpServletRequest request) {
        logger.info("Cancel reservation for accommodation {} ", accommodationId);
        User user = userService.getUser(request);
        return reservationService.getFutureReservationsForHost(user, accommodationId);
    }
}
