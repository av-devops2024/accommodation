package com.devops.accommodation.controller;

import com.devops.accommodation.service.interfaces.IRatingService;
import com.devops.accommodation.service.interfaces.IUserService;
import ftn.devops.db.User;
import ftn.devops.dto.request.RatingRequest;
import ftn.devops.dto.response.RatingResponse;
import ftn.devops.dto.response.RatingSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private IRatingService ratingService;
    @Autowired
    private IUserService userService;

    @PostMapping("/accommodation/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse addAccommodationRating(@PathVariable Long accommodationId, @RequestBody RatingRequest ratingRequest,
                                                        HttpServletRequest request){
        User user = userService.getUser(request);
        return ratingService.addAccommodationRating(user, accommodationId, ratingRequest);
    }

    @PostMapping("/accommodation/edit/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse editAccommodationRating(@PathVariable Long accommodationId, @RequestBody RatingResponse rating,
                                                        HttpServletRequest request){
        User user = userService.getUser(request);
        return ratingService.editAccommodationRating(user, accommodationId, rating);
    }

    @DeleteMapping("/accommodation/{accommodationId}/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse deleteAccommodationRating(@PathVariable Long accommodationId, @PathVariable Long ratingId,
                                                        HttpServletRequest request){
        User user = userService.getUser(request);
        return ratingService.deleteAccommodationRating(user, accommodationId, ratingId);
    }

    @PostMapping("/host/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse addHostRating(@PathVariable Long userId, @RequestBody RatingRequest rating,
                                               HttpServletRequest request){
        User user = userService.getUser(request);
        return ratingService.addHostRating(user, userId, rating);
    }

    @PostMapping("/host/edit/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse editHostRating(@PathVariable Long userId, @RequestBody RatingResponse ratingRequest,
                                               HttpServletRequest request){
        User user = userService.getUser(request);
        return ratingService.editHostRating(user, userId, ratingRequest);
    }

    @DeleteMapping("/host/{userId}/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse deleteHostRating(@PathVariable Long userId,  @PathVariable Long ratingId,
                                               HttpServletRequest request){
        User user = userService.getUser(request);
        return ratingService.deleteHostRating(user, userId, ratingId);
    }

    @GetMapping("/accommodation/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse getAccommodationRatings(@PathVariable Long accommodationId){
        return ratingService.getAccommodationRatings(accommodationId);
    }

    @GetMapping("/host/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingSummaryResponse getHostRatings(@PathVariable Long userId){
        return ratingService.getHostRatings(userId);
    }
}
