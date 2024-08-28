package com.devops.accommodation.controller;

import com.devops.accommodation.dto.request.CreateReservationRequest;
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
@RequestMapping("/reservationRequest")
public class ReservationRequestController {

    @Autowired
    private IReservationService reservationRequestService;

    @Autowired
    private IUserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> addReservationRequest(HttpServletRequest request, @RequestBody CreateReservationRequest reservationRequest){
        User user = userService.getUser(request);
        return reservationRequestService.addReservationRequest(user, reservationRequest);
    }

    @GetMapping("/delete/{reservationRequestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> deleteReservationRequest(HttpServletRequest request, @PathVariable long reservationRequestId){
        User user = userService.getUser(request);
        return reservationRequestService.deleteReservationRequest(user, reservationRequestId);
    }

    @GetMapping("/accept/{reservationRequestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<HostReservationDTO> acceptReservationRequest(HttpServletRequest request, @PathVariable long reservationRequestId) throws DataFormatException, IOException {
        User user = userService.getUser(request);
        return reservationRequestService.acceptReservationRequest(user, reservationRequestId);
    }

    @GetMapping("host")
    @ResponseStatus(HttpStatus.OK)
    public List<HostReservationDTO> getReservationRequestsForHost(HttpServletRequest request) throws DataFormatException, IOException {
        User user = userService.getUser(request);
        return reservationRequestService.getReservationRequestsForHost(user);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> getReservationRequestsForGuest(HttpServletRequest request){
        User user = userService.getUser(request);
        return reservationRequestService.getReservationRequestsForGuest(user);
    }
}
