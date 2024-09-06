package com.devops.accommodation.controller;

import com.devops.accommodation.service.interfaces.IRatingService;
import com.devops.accommodation.service.interfaces.IUserService;
import ftn.devops.db.User;
import ftn.devops.dto.request.RatingRequest;
import ftn.devops.dto.response.GuestRatingReservationDTO;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.RatingResponse;
import ftn.devops.dto.response.RatingSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private IRatingService ratingService;
    @Autowired
    private IUserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public RatingResponse addAccommodationRating(@RequestBody RatingRequest ratingRequest,
                                                        HttpServletRequest request){
        logger.info("Add rating - {}", applicationName);
        User user = userService.getUser(request);
        return ratingService.addAccommodationRating(user, ratingRequest);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public RatingResponse editAccommodationRating(@RequestBody RatingResponse rating,
                                                        HttpServletRequest request){
        logger.info("Edit rating - {}", applicationName);
        User user = userService.getUser(request);
        return ratingService.editAccommodationRating(user, rating);
    }

    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestRatingReservationDTO> deleteAccommodationRating( @PathVariable Long ratingId,
                                                        HttpServletRequest request){
        logger.info("Delete rating - {}", applicationName);
        User user = userService.getUser(request);
        return ratingService.deleteAccommodationRating(user, ratingId);
    }

    @GetMapping("/accommodation/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse getAccommodationRatings(@PathVariable Long accommodationId){
        logger.info("Get ratings for accommodation {}", accommodationId);
        return ratingService.getAccommodationRatings(accommodationId);
    }

    @GetMapping("/host/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse getHostRatings(@PathVariable Long userId){
        logger.info("Get host ratings {}", userId);
        return ratingService.getHostRatings(userId);
    }

    @GetMapping("/ratedReservations")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestRatingReservationDTO> getRatedReservations(HttpServletRequest request){
        logger.info("Get rated reservations - {}", applicationName);
        User user = userService.getUser(request);
        return ratingService.getRatedReservations(user);
    }

    @GetMapping("/unratedReservations")
    @ResponseStatus(HttpStatus.OK)
    public List<GuestReservationDTO> getUnratedReservations(HttpServletRequest request){
        logger.info("Get unrated reservations - {}", applicationName);
        User user = userService.getUser(request);
        return ratingService.getUnratedReservations(user);
    }
}
