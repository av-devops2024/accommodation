package com.devops.accommodation.service.interfaces;

import ftn.devops.db.User;
import ftn.devops.dto.request.RatingRequest;
import ftn.devops.dto.response.RatingResponse;
import ftn.devops.dto.response.RatingSummaryResponse;

public interface IRatingService {
    RatingSummaryResponse addAccommodationRating(User user, Long accommodationId, RatingRequest ratingRequest);

    RatingSummaryResponse addHostRating(User user, Long userId, RatingRequest ratingRequest);

    RatingSummaryResponse getAccommodationRatings(Long accommodationId);

    RatingSummaryResponse getHostRatings(Long userId);

    RatingSummaryResponse deleteAccommodationRating(User user, Long accommodationId, Long ratingId);

    RatingSummaryResponse deleteHostRating(User user, Long userId, Long ratingId);

    RatingSummaryResponse editAccommodationRating(User user, Long accommodationId, RatingResponse rating);

    RatingSummaryResponse editHostRating(User user, Long userId, RatingResponse ratingRequest);
}
