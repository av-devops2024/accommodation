package com.devops.accommodation.service.interfaces;

import ftn.devops.db.User;
import ftn.devops.dto.request.RatingRequest;
import ftn.devops.dto.response.GuestRatingReservationDTO;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.RatingResponse;
import ftn.devops.dto.response.RatingSummaryResponse;

import java.util.List;

public interface IRatingService {
    RatingResponse addAccommodationRating(User user, RatingRequest ratingRequest);

    RatingSummaryResponse getAccommodationRatings(Long accommodationId);

    RatingSummaryResponse getHostRatings(Long userId);

    List<GuestRatingReservationDTO> deleteAccommodationRating(User user, Long ratingId);

    RatingResponse editAccommodationRating(User user, RatingResponse ratingDTO);

    List<GuestRatingReservationDTO> getRatedReservations(User user);

    List<GuestReservationDTO> getUnratedReservations(User user);
}
