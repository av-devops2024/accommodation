package com.devops.accommodation.service.implementation;

import com.devops.accommodation.exception.ActionNotAllowedException;
import com.devops.accommodation.repository.RatingRepository;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IRatingService;
import com.devops.accommodation.service.interfaces.IReservationService;
import com.devops.accommodation.service.interfaces.IUserService;
import com.devops.accommodation.utils.Constants;
import ftn.devops.db.Accommodation;
import ftn.devops.db.Rating;
import ftn.devops.db.User;
import ftn.devops.dto.request.RatingRequest;
import ftn.devops.dto.response.RatingResponse;
import ftn.devops.dto.response.RatingSummaryResponse;
import ftn.devops.enums.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import org.apache.catalina.Host;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class RatingService implements IRatingService {
    @Autowired
    private LogClientService logClientService;

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private IReservationService reservationService;
    @Autowired
    private IAccommodationService accommodationService;
    @Autowired
    private IUserService userService;

    @Override
    public RatingSummaryResponse addAccommodationRating(User user, Long accommodationId, RatingRequest ratingRequest) {
        if (reservationService.hadReservationInAccommodation(user, accommodationId)){
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_USER_HAD_NO_RESERVATION);
        }
        if (ratingRepository.findByGuest_IdAndAccommodation_Id(user.getId(), accommodationId) != null)
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_USER_ALREADY_HAS_RESERVATION_FOR_THE_ENTITY);
        addRatingForAccommodation(user, ratingRequest, accommodationId);
        return getAccommodationRatings(accommodationId);
    }
    @Override
    public RatingSummaryResponse addHostRating(User user, Long userId, RatingRequest ratingRequest) {
        if (reservationService.hadReservationFromHost(user, userId)){
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_USER_HAD_NO_RESERVATION);
        }
        if (ratingRepository.findByGuest_IdAndHost_Id(user.getId(), userId) != null)
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_USER_ALREADY_HAS_RESERVATION_FOR_THE_ENTITY);
        addRatingForHost(user, ratingRequest, userId);
        return getHostRatings(userId);
    }

    @Override
    public RatingSummaryResponse editAccommodationRating(User user, Long accommodationId, RatingResponse ratingDTO) {
        Rating rating = findById(ratingDTO.getId());
        if (!Objects.equals(rating.getGuest().getId(), user.getId()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_RATING_IS_NOT_FROM_USER);
        updateRating(rating, ratingDTO);
        return getAccommodationRatings(accommodationId);
    }

    @Override
    public RatingSummaryResponse editHostRating(User user, Long userId, RatingResponse ratingDTO) {
        Rating rating = findById(ratingDTO.getId());
        if (!Objects.equals(rating.getGuest().getId(), user.getId()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_RATING_IS_NOT_FROM_USER);
        updateRating(rating, ratingDTO);
        return getHostRatings(userId);
    }

    @Override
    public RatingSummaryResponse deleteAccommodationRating(User user, Long accommodationId, Long ratingId) {
        Rating rating = findById(ratingId);
        if (!Objects.equals(rating.getGuest().getId(), user.getId()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_RATING_IS_NOT_FROM_USER);
        ratingRepository.delete(rating);
        return getAccommodationRatings(accommodationId);
    }

    @Override
    public RatingSummaryResponse deleteHostRating(User user, Long userId, Long ratingId) {
        Rating rating = findById(ratingId);
        if (!Objects.equals(rating.getGuest().getId(), user.getId()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_RATING_IS_NOT_FROM_USER);
        ratingRepository.delete(rating);
        return getHostRatings(userId);
    }

    @Override
    public RatingSummaryResponse getAccommodationRatings(Long accommodationId) {
        return convertIntoResponse(ratingRepository.findByAccommodation_Id(accommodationId));
    }

    @Override
    public RatingSummaryResponse getHostRatings(Long userId) {
        return convertIntoResponse(ratingRepository.findByHost_Id(userId));
    }

    private void updateRating(Rating rating, RatingResponse ratingDTO) {
        rating.setText(ratingDTO.getText());
        rating.setValue(ratingDTO.getValue());
        ratingRepository.save(rating);
    }

    public Rating findById(long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(Constants.RATING_NOT_FOUND);
                });
    }

    private RatingSummaryResponse convertIntoResponse(List<Rating> ratings) {
        RatingSummaryResponse response = new RatingSummaryResponse();
        double meanRating = 0;
        for (Rating rating : ratings) {
            meanRating = meanRating + rating.getValue();
            Hibernate.initialize(rating.getGuest());
            response.addRatingResponse(rating);
        }
        response.setMeanRating(meanRating/ratings.size());
        return response;
    }

    private void addRatingForAccommodation(User user, RatingRequest ratingRequest, long accommodationId) {
        Rating rating = new Rating();
        Accommodation accommodation = accommodationService.getAccommodationById(accommodationId);
        rating.setAccommodation(accommodation);
        rating.setDate(LocalDateTime.now());
        rating.setGuest(user);
        rating.setText(ratingRequest.getText());
        rating.setValue(ratingRequest.getValue());
        ratingRepository.save(rating);
        logClientService.sendNotificationWithGuestName(accommodation.getHost(), user.getName(), accommodation, null, NotificationType.RATING_ACCOMMODATION);
    }

    private void addRatingForHost(User user, RatingRequest ratingRequest, long userId) {
        Rating rating = new Rating();
        User host = userService.findById(userId);
        rating.setHost(host);
        rating.setDate(LocalDateTime.now());
        rating.setGuest(user);
        rating.setText(ratingRequest.getText());
        rating.setValue(ratingRequest.getValue());
        ratingRepository.save(rating);
        logClientService.sendNotificationWithGuestName(host, user.getName(), null, null, NotificationType.RATING_HOST);
    }
}
