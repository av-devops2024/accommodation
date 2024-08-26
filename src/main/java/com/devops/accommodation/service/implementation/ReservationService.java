package com.devops.accommodation.service.implementation;

import com.devops.accommodation.aspect.TrackExecutionTime;
import com.devops.accommodation.dto.response.HostFutureReservationResponse;
import com.devops.accommodation.exception.ActionNotAllowedException;
import com.devops.accommodation.exception.InvalidRelationshipException;
import com.devops.accommodation.repository.AccommodationRepository;
import com.devops.accommodation.repository.ReservationRepository;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IPriceService;
import com.devops.accommodation.service.interfaces.IReservationService;
import com.devops.accommodation.utils.Constants;
import com.devops.accommodation.utils.DateUtils;
import ftn.devops.db.Accommodation;
import ftn.devops.db.Price;
import ftn.devops.db.Reservation;
import ftn.devops.db.User;
import ftn.devops.dto.request.CreateReservationRequestDTO;
import ftn.devops.dto.response.GuestReservationDTO;
import ftn.devops.dto.response.HostReservationDTO;
import ftn.devops.enums.NotificationType;
import ftn.devops.enums.PriceType;
import ftn.devops.log.LogType;
import jakarta.persistence.EntityNotFoundException;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ReservationService implements IReservationService {

    @Autowired
    protected LogClientService logClientService;
    @Autowired
    protected ReservationRepository reservationRepository;

    @Autowired
    private IAccommodationService accommodationService;

    @Autowired
    private IPriceService priceService;
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Override
    public boolean hasApprovedReservationInside(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate) {
        return reservationRepository
                .existsByAccommodation_IdAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndCancelledFalseAndApprovedTrueAndDeletedFalse(accommodationId, fromDate, toDate);
    }

    @Override
    public boolean hasApprovedReservationIntersect(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate){
    return reservationRepository.existsByAccommodation_IdAndStartDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(accommodationId, fromDate, toDate)
                || reservationRepository.existsByAccommodation_IdAndEndDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(accommodationId, fromDate, toDate)
                || reservationRepository.existsByAccommodation_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndApprovedTrueAndCancelledFalseAndDeletedFalse(accommodationId, fromDate, toDate);
    }

    @Override
    public Reservation findById(long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> {
                    logClientService.sendLog(LogType.WARN, "Reservation not found", id);
                    throw new EntityNotFoundException(Constants.RESERVATION_NOT_FOUND);
                });
    }

    @Override
    public List<Reservation> getIntersectedReservations(long accommodationId, LocalDateTime fromDate, LocalDateTime toDate) {
        List<Reservation> result = new ArrayList<>();
        List<Reservation> reservationsBefore = reservationRepository
                .findByAccommodation_IdAndStartDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(accommodationId, fromDate, toDate);
        List<Reservation> reservationsAfter = reservationRepository
                .findByAccommodation_IdAndEndDateBetweenAndApprovedTrueAndCancelledFalseAndDeletedFalse(accommodationId, fromDate, toDate);
        List<Reservation> reservationsAround = reservationRepository
                .findByAccommodation_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndApprovedTrueAndCancelledFalseAndDeletedFalse(accommodationId, fromDate, toDate);
        result.addAll(reservationsBefore);
        result.addAll(reservationsAfter);
        result.addAll(reservationsAround);
        return result;
    }

    @Override
    @TrackExecutionTime
    public List<GuestReservationDTO> addReservationRequest(User guest, CreateReservationRequestDTO reservationRequest) {
        logClientService.sendLog(LogType.INFO, "Add Reservation request", new Object[]{guest.getId(), reservationRequest});
        DateUtils.checkDateValidity(reservationRequest.getStartDate(), reservationRequest.getEndDate());

        if (hasApprovedReservationIntersect(reservationRequest.getAccommodationId(), reservationRequest.getStartDate(), reservationRequest.getEndDate())){
            logClientService.sendLog(LogType.INFO, Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION, new Object[]{guest.getId(), reservationRequest});
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);
        }

        Accommodation accommodation = accommodationService.getAccommodationById(reservationRequest.getAccommodationId());
        if (reservationRequest.getNumberOfGuests() > accommodation.getMaxNumberOfGuests()) {
            logClientService.sendLog(LogType.INFO, Constants.TOO_HIGH_GUEST_NUMBER, new Object[]{guest.getId(), reservationRequest});
            throw new ActionNotAllowedException(Constants.TOO_HIGH_GUEST_NUMBER);
        }
        if (reservationRequest.getNumberOfGuests() < accommodation.getMinNumberOfGuests()){
            logClientService.sendLog(LogType.INFO, Constants.TOO_LOW_GUEST_NUMBER, new Object[]{guest.getId(), reservationRequest});
            throw new ActionNotAllowedException(Constants.TOO_LOW_GUEST_NUMBER);
        }

        double price = countPrice(reservationRequest.getAccommodationId(), reservationRequest.getStartDate(),
                reservationRequest.getEndDate(), reservationRequest.getNumberOfGuests()).b;
        Reservation reservation = createReservation(reservationRequest, accommodation, guest, price, accommodation.isAutomaticallyAcceptRequest());
        logClientService.sendNotification(guest, accommodation, reservation, NotificationType.RESERVATION_REQUEST);
        return getReservationRequestsForGuest(guest);
    }

    @Override
    @TrackExecutionTime
    public List<GuestReservationDTO> deleteReservationRequest(User user, long reservationRequestId) {
        logClientService.sendLog(LogType.INFO, "Delete reservation request", new Object[]{user.getId(), reservationRequestId});
        Reservation reservation = findById(reservationRequestId);
        if (!Objects.equals(reservation.getGuest().getId(), user.getId())){
            logClientService.sendLog(LogType.INFO, Constants.INVALID_RESERVATION_DELETE, new Object[]{user.getId(), reservationRequestId});
            throw new InvalidRelationshipException(Constants.INVALID_RESERVATION_DELETE);
        }

        if (reservation.isApproved()) {
            logClientService.sendLog(LogType.INFO, Constants.RESERVATION_CAN_NOT_BE_DELETED_AFTER_IT_GOT_APPROVED, new Object[]{user.getId(), reservationRequestId});
            throw new ActionNotAllowedException(Constants.RESERVATION_CAN_NOT_BE_DELETED_AFTER_IT_GOT_APPROVED);
        }
        reservation.setDeleted(true);
        reservationRepository.save(reservation);
        return getReservationRequestsForGuest(user);
    }

    @Override
    @TrackExecutionTime
    public List<GuestReservationDTO> cancelReservation(User user, long reservationId) {
        logClientService.sendLog(LogType.INFO, "Cancel reservation", new Object[]{user.getId(), reservationId});
        Reservation reservationRequest = findById(reservationId);
        if (!Objects.equals(reservationRequest.getGuest().getId(), user.getId())) {
            logClientService.sendLog(LogType.INFO, Constants.INVALID_RESERVATION_CANCEL, new Object[]{user.getId(), reservationId});
            throw new InvalidRelationshipException(Constants.INVALID_RESERVATION_CANCEL);
        }
        if (reservationRequest.getStartDate().isBefore(LocalDateTime.now())){
            logClientService.sendLog(LogType.INFO, Constants.INVALID_RESERVATION_CANCEL_TIME, new Object[]{user.getId(), reservationId});
            throw new ActionNotAllowedException(Constants.INVALID_RESERVATION_CANCEL_TIME);
        }
        reservationRequest.setCancelled(true);
        reservationRepository.save(reservationRequest);
        logClientService.sendNotification(reservationRequest.getAccommodation().getHost(), reservationRequest.getAccommodation(), reservationRequest, NotificationType.RESERVATION_CANCELLED);
        return getReservationsForGuest(user);
    }

    @Override
    @TrackExecutionTime
    public List<HostReservationDTO> acceptReservationRequest(User user, long reservationRequestId) {
        logClientService.sendLog(LogType.INFO, "Accept reservation", new Object[]{user.getId(), reservationRequestId});
        Reservation reservationRequest = findById(reservationRequestId);
        if (!Objects.equals(reservationRequest.getAccommodation().getHost().getId(), user.getId())){
            logClientService.sendLog(LogType.INFO, Constants.INVALID_RESERVATION_ACCEPT, new Object[]{user.getId(), reservationRequestId});
            throw new InvalidRelationshipException(Constants.INVALID_RESERVATION_ACCEPT);
        }

        if (hasApprovedReservationIntersect(reservationRequest.getAccommodation().getId(), reservationRequest.getStartDate(), reservationRequest.getEndDate())){
            logClientService.sendLog(LogType.INFO, Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION, new Object[]{user.getId(), reservationRequestId});
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);
        }
        reservationRequest.setApproved(true);
        reservationRepository.save(reservationRequest);

        getIntersectedReservations(reservationRequest.getAccommodation().getId(), reservationRequest.getStartDate(), reservationRequest.getEndDate())
                .forEach(res -> {
                    if (!Objects.equals(res.getId(), reservationRequest.getId()) && !res.isDeleted()) {
                        res.setDeleted(true);
                        reservationRepository.save(res);
                        logClientService.sendNotification(res.getGuest(), res.getAccommodation(), res, NotificationType.RESERVATION_ANSWER);
                    }
                });
        logClientService.sendNotification(reservationRequest.getGuest(), reservationRequest.getAccommodation(), reservationRequest, NotificationType.RESERVATION_ANSWER);
        return getReservationRequestsForHost(user, reservationRequest.getAccommodation().getId());
    }

    @Override
    @TrackExecutionTime
    public List<GuestReservationDTO> getReservationsForGuest(User user) {
        logClientService.sendLog(LogType.INFO, "Get reservations for user", user.getId());
        return getGuestReservationRequestDTOs(reservationRepository
                .findByGuest_IdAndStartDateAfterAndApprovedTrueAndDeletedFalseAndCancelledFalse(user.getId(), LocalDateTime.now()));
    }

    @Override
    @TrackExecutionTime
    public List<HostReservationDTO> getReservationsForHost(User user, int accommodationId) {
        logClientService.sendLog(LogType.INFO, "Get reservations for accommodation", new Object[]{user.getId(), accommodationId} );
        Accommodation accommodation = accommodationService.getAccommodationById(accommodationId);
        if (!Objects.equals(accommodation.getHost().getId(), user.getId())){
            logClientService.sendLog(LogType.INFO, Constants.INVALID_ACCOMMODATION_HOST_RELATIONSHIP, new Object[]{user.getId(), accommodationId} );
            throw new InvalidRelationshipException(Constants.INVALID_ACCOMMODATION_HOST_RELATIONSHIP);
        }
        return getHostReservationDTOs(reservationRepository.findByAccommodation_IdAndStartDateAfterAndApprovedTrueAndDeletedFalseAndCancelledFalse(accommodationId, LocalDateTime.now()));
    }

    @Override
    public HostFutureReservationResponse getFutureReservationsForHost(User user, Long accommodationId) {
        List<LocalDateTime> dates = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findByAccommodationIdAndCancelledFalseAndDeletedFalseAndApprovedTrueAndStartDateAfter(accommodationId, LocalDateTime.now());
        for(Reservation reservation : reservations) {
            LocalDateTime startDate = reservation.getStartDate();
            LocalDateTime endDate = reservation.getEndDate();

            for (LocalDateTime date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if(!dates.contains(date)) {
                    dates.add(date);
                }
            }
        }

        return new HostFutureReservationResponse(dates);
    }

    @Override
    @TrackExecutionTime
    public List<HostReservationDTO> getReservationRequestsForHost(User user, long accommodationId) {
        logClientService.sendLog(LogType.INFO, "Get reservation requests for accommodation", new Object[]{user.getId(), accommodationId} );
        Accommodation accommodation = accommodationService.getAccommodationById(accommodationId);
        if (!Objects.equals(accommodation.getHost().getId(), user.getId())){
            logClientService.sendLog(LogType.INFO, Constants.INVALID_ACCOMMODATION_HOST_RELATIONSHIP, new Object[]{user.getId(), accommodationId} );
            throw new InvalidRelationshipException(Constants.INVALID_ACCOMMODATION_HOST_RELATIONSHIP);
        }
        return getHostReservationDTOs(reservationRepository.findByAccommodation_IdAndStartDateAfterAndApprovedFalseAndDeletedFalse(accommodationId, LocalDateTime.now()));
    }

    @Override
    @TrackExecutionTime
    public List<GuestReservationDTO> getReservationRequestsForGuest(User user) {
        logClientService.sendLog(LogType.INFO, "Get reservation requests for user", user.getId());
        return getGuestReservationRequestDTOs(reservationRepository.findByGuest_IdAndStartDateAfterAndApprovedFalseAndDeletedFalse(user.getId(), LocalDateTime.now()));
    }

    protected List<HostReservationDTO> getHostReservationDTOs(List<Reservation> reservationRequests) {
        List<HostReservationDTO> slots = new ArrayList<>();
        reservationRequests.forEach(slot -> {
                    Pair<Long, Long> guestStatistics = getGuestStatistics(slot.getGuest().getId());
                    slots.add(new HostReservationDTO(slot, guestStatistics.a, guestStatistics.b));
                });
        Collections.sort(slots);
        return slots;
    }

    protected Pair<Long, Long> getGuestStatistics(long guestId){
        long numberOfReservations = reservationRepository.countByGuest_IdAndDeletedFalseAndApprovedTrue(guestId);
        long numberOfCanceledReservations = reservationRepository.countByGuest_IdAndDeletedFalseAndApprovedTrueAndCancelledTrue(guestId);
        return new Pair<>(numberOfReservations,numberOfCanceledReservations);
    }

    protected List<GuestReservationDTO> getGuestReservationRequestDTOs(List<Reservation> reservationRequests) {
        List<GuestReservationDTO> slots = new ArrayList<>();
        reservationRequests.forEach(slot -> slots.add(new GuestReservationDTO(slot)));
        Collections.sort(slots);
        return slots;
    }

    @Override
    public Pair<Price, Double> countPrice(long accommodationId, LocalDateTime startDate, LocalDateTime endDate, int numberOfGuest) {
        Price price = priceService.findByInterval(accommodationId, startDate, endDate);
        if (price.getType() == PriceType.PER_ACCOMMODATION)
            return new Pair<>(price, ChronoUnit.DAYS.between(startDate, endDate) * price.getValue());
        else if (price.getType() == PriceType.PER_PERSON)
            return new Pair<>(price, price.getValue() * numberOfGuest * ChronoUnit.DAYS.between(startDate, endDate));
        throw new ActionNotAllowedException(Constants.NOT_EXISTING_PRICE_TYPE);
    }

    private Reservation createReservation(CreateReservationRequestDTO reservationRequest,
                                   Accommodation accommodation,
                                   User guest,
                                   double price,
                                   boolean approved) {
        Reservation reservation = new Reservation();
        reservation.setDeleted(false);
        reservation.setStartDate(reservationRequest.getStartDate());
        reservation.setEndDate(reservationRequest.getEndDate());
        reservation.setApproved(approved);
        reservation.setCancelled(false);
        reservation.setAccommodation(accommodation);
        reservation.setGuest(guest);
        reservation.setNumberOfGuests(reservationRequest.getNumberOfGuests());
        reservation.setPrice(price);
        return reservationRepository.save(reservation);
    }
}
