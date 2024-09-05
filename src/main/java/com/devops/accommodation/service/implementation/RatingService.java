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
import ftn.devops.db.Reservation;
import ftn.devops.db.User;
import ftn.devops.dto.request.RatingRequest;
import ftn.devops.dto.response.GuestRatingReservationDTO;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.RatingResponse;
import ftn.devops.dto.response.RatingSummaryResponse;
import ftn.devops.enums.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class RatingService implements IRatingService {
    private final Logger logger = LoggerFactory.getLogger(RatingService.class);
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
    public RatingResponse addAccommodationRating(User user, RatingRequest ratingRequest) {
        Reservation reservation = reservationService.findById(ratingRequest.getReservationId());
        Accommodation accommodation = reservation.getAccommodation();
        if (reservationService.hadPastReservationInAccommodation(user, accommodation.getId())){
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_USER_HAD_NO_RESERVATION);
        }
        //treba proveriti da li ima za rezervaciju, a ne za accommodation, za accommodation moze imati vise puta
//        if (ratingRepository.findByGuest_IdAndAccommodation_Id(user.getId(), accommodationId) != null)
//            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_USER_ALREADY_HAS_RESERVATION_FOR_THE_ENTITY);
        Rating rating = addRatingForAccommodation(user, ratingRequest, accommodation);
        reservation.setRated(true);
        reservation.setRatingId(rating.getId());
        reservationService.save(reservation);
        return new RatingResponse(rating);
    }



    @Override
    public RatingResponse editAccommodationRating(User user, RatingResponse ratingDTO) {
        Rating rating = findById(ratingDTO.getId());
        if (!Objects.equals(rating.getGuest().getId(), user.getId()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_RATING_IS_NOT_FROM_USER);
        Rating updatedRating = updateRating(rating, ratingDTO);
        return new RatingResponse(updatedRating);
    }

    @Override
    public List<GuestRatingReservationDTO> getRatedReservations(User user) {
        logger.info("Get rated reservations for user {}",user.getId());
        return getGuestRatingReservationDTOs(reservationService
                .getRatedReservations(user));
    }

    @Override
    public List<GuestReservationDTO> getUnratedReservations(User user) {
        logger.info("Get unrated reservations for user {}",user.getId());
        return getGuestReservationRequestDTOs(reservationService
                .getUnratedReservations(user));
    }

    @Override
    public List<GuestRatingReservationDTO> deleteAccommodationRating(User user, Long ratingId) {
        Rating rating = findById(ratingId);
        if (!Objects.equals(rating.getGuest().getId(), user.getId()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_RATING_IS_NOT_FROM_USER);
        Reservation reservation = reservationService.findByRatingId(ratingId);
        reservation.setRatingId(null);
        reservation.setRated(false);
        reservationService.save(reservation);
        ratingRepository.delete(rating);
        return getRatedReservations(user);
    }

    @Override
    public RatingSummaryResponse getAccommodationRatings(Long accommodationId) {
        return convertIntoResponse(ratingRepository.findByAccommodation_Id(accommodationId), false);
    }

    @Override
    public RatingSummaryResponse getHostRatings(Long userId) {
        return convertIntoResponse(ratingRepository.findByHost_Id(userId), true);
    }

    private Rating updateRating(Rating rating, RatingResponse ratingDTO) {
        rating.setAccommodationValue(ratingDTO.getAccommodationValue());
        rating.setAccommodationText(ratingDTO.getAccommodationText());
        rating.setHostValue(ratingDTO.getHostValue());
        rating.setHostText(ratingDTO.getHostText());
        rating.setDate(LocalDateTime.now());
        return ratingRepository.save(rating);
    }

    public Rating findById(long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(Constants.RATING_NOT_FOUND);
                });
    }

    protected List<GuestRatingReservationDTO> getGuestRatingReservationDTOs(List<Reservation> reservationRequests) {
        List<GuestRatingReservationDTO> slots = new ArrayList<>();
        for(Reservation reservation : reservationRequests) {
            Rating rating = findById(reservation.getRatingId());
            slots.add(new GuestRatingReservationDTO(reservation, rating));
        }
        Collections.sort(slots);
        return slots;
    }

    protected List<GuestReservationDTO> getGuestReservationRequestDTOs(List<Reservation> reservationRequests) {
        List<GuestReservationDTO> slots = new ArrayList<>();
        reservationRequests.forEach(slot -> slots.add(new GuestReservationDTO(slot)));
        Collections.sort(slots);
        return slots;
    }

    private RatingSummaryResponse convertIntoResponse(List<Rating> ratings, boolean isHost) {
        RatingSummaryResponse response = new RatingSummaryResponse();
        double meanRating = 0;
        for (Rating rating : ratings) {
            double value = isHost ? rating.getHostValue() : rating.getAccommodationValue();
            meanRating = meanRating + value;
            Hibernate.initialize(rating.getGuest());
            response.addRatingResponse(rating);
        }
        response.setMeanRating(meanRating/ratings.size());
        return response;
    }

    private Rating addRatingForAccommodation(User user, RatingRequest ratingRequest, Accommodation accommodation) {
        Rating rating = new Rating();
        rating.setAccommodation(accommodation);
        rating.setDate(LocalDateTime.now());
        rating.setGuest(user);
        rating.setAccommodationText(ratingRequest.getAccommodationText());
        rating.setAccommodationValue(ratingRequest.getAccommodationValue());
        rating.setHostText(ratingRequest.getHostText());
        rating.setHostValue(ratingRequest.getHostValue());
        rating.setHost(accommodation.getHost());
        Rating savedRating = ratingRepository.save(rating);
        logClientService.sendNotificationWithGuestName(accommodation.getHost(), user.getName(), accommodation, null, NotificationType.RATING_ACCOMMODATION);
        return savedRating;
    }
}
