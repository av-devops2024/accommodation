package com.devops.accommodation.service.implementation;

import com.devops.accommodation.aspect.TrackExecutionTime;
import com.devops.accommodation.dto.request.PriceRequest;
import com.devops.accommodation.exception.ActionNotAllowedException;
import com.devops.accommodation.exception.EntityNotFoundException;
import com.devops.accommodation.exception.InvalidDateException;
import com.devops.accommodation.repository.PriceRepository;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IPriceService;
import com.devops.accommodation.service.interfaces.IReservationService;
import com.devops.accommodation.utils.Constants;
import com.devops.accommodation.utils.DateUtils;
import ftn.devops.db.Price;
import ftn.devops.dto.DateRangeDTO;
import ftn.devops.dto.PriceDTO;
import ftn.devops.enums.PriceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PriceService implements IPriceService {
    private final Logger logger = LoggerFactory.getLogger(PriceService.class);

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private IReservationService reservationService;
    @Autowired
    private IAccommodationService accommodationService;

    @Override
    public PriceDTO addPrice(Long accommodationId, PriceRequest priceRequest) {
        logger.info("Add price");
        this.checkDateValidity(priceRequest.getStartDate(), priceRequest.getEndDate());

//        if (reservationService.hasApprovedReservationInside(accommodationId, priceRequest.getStartDate(), priceRequest.getEndDate())){
////            logClientService.sendLog(LogType.WARN, "Reservation is present in period", new Object[]{accommodationId, priceDTO});
//            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);
//        }
//        checkPriceRangeOverlapping(accommodationId, priceRequest);

        Price price = createPrice(priceRequest, accommodationId);
        Price savedPrice = priceRepository.save(price);
        logger.info("Price is added");
        return new PriceDTO(savedPrice);
    }

    @Override
    @TrackExecutionTime
    public List<PriceDTO> updatePrice(Long accommodationId, PriceDTO priceDTO) {
        logger.info("Update price");
        this.checkDateValidity(priceDTO.getStartDate(), priceDTO.getEndDate());

        if (reservationService.hasApprovedReservationInside(accommodationId, priceDTO.getStartDate(), priceDTO.getEndDate())) {
            logger.warn("Reservation is present in period");
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);
        }

        Price price = getPrice(priceDTO.getId());
        if (!price.getStartDate().equals(priceDTO.getStartDate()) || !price.getEndDate().equals(priceDTO.getEndDate())) {
            logger.warn("Price range can not be changed");
            throw new ActionNotAllowedException(Constants.PRICE_START_AND_END_DATE_ARE_NOT_CHANGEABLE);
        }

        price.setValue(priceDTO.getValue());
        price.setType(PriceType.valueOf(priceDTO.getType()));
        priceRepository.save(price);
        logger.info("Price is updated");
        return getPrices(accommodationId);
    }

    @Override
    public List<PriceDTO> getPrices(Long accommodationId) {
        logger.info("Get prices");
        List<Price> prices = priceRepository.findByAccommodation_IdAndDeletedFalseOrderByStartDateAsc(accommodationId);
        return getPriceDTOs(prices);
    }

    @Override
    public List<PriceDTO> getActivePrices(Long accommodationId) {
        logger.info("Get active prices");
        List<Price> prices = priceRepository.findByAccommodation_IdAndDeletedFalseAndEndDateGreaterThanOrderByStartDateAsc(accommodationId,  LocalDateTime.now());
        return getPriceDTOs(prices);
    }

    @Override
    @TrackExecutionTime
    public List<PriceDTO> deletePrices(Long accommodationId, PriceDTO priceDTO) {
        Price price = getPrice(priceDTO.getId());
        if (accommodationId != price.getAccommodation().getId()){
            logger.warn("Price does not belong to accommodation");
            throw new EntityNotFoundException(Constants.INVALID_PRICE_ACCOMMODATION_RELATIONSHIP);
        }
        price.setDeleted(true);
        priceRepository.save(price);
        logger.info("Price is deleted");
        return getPrices(accommodationId);
    }

    @Override
    public Price findByInterval(long accommodationId, LocalDateTime startDate, LocalDateTime endDate) {
        return priceRepository.findByAccommodation_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(accommodationId, startDate, endDate)
                .orElseThrow(() -> {throw new EntityNotFoundException(Constants.PRICE_RANGE_NOT_FOUND);} );
    }

    private Price createPrice(PriceRequest priceRequest, long accommodationId) {
        Price price = new Price();
        price.setDeleted(false);
        price.setEndDate(priceRequest.getEndDate());
        price.setStartDate(priceRequest.getStartDate());
        price.setType(PriceType.valueOf(priceRequest.getType()));
        price.setValue(priceRequest.getValue());
        price.setAccommodation(accommodationService.getAccommodationById(accommodationId));
        return price;
    }

    private void checkPriceRangeOverlapping(long accommodationId, PriceRequest priceRequest) {
        List<PriceDTO> prices = getActivePrices(accommodationId);
        List<DateRangeDTO> dateRanges = new ArrayList<>();
        prices.forEach(price -> dateRanges.add(new DateRangeDTO(price.getStartDate(), price.getEndDate())));
        dateRanges.add(new DateRangeDTO(priceRequest.getStartDate(), priceRequest.getEndDate()));
        if (DateUtils.checkIfContainsOverlapping(dateRanges)){
//            logClientService.sendLog(LogType.WARN, "Price ranges are overlapping", new Object[]{accommodationId, priceDTO});
            throw new InvalidDateException(Constants.OVERLAPPING_INTERVALS);
        }
    }

    private Price getPrice(long id){
        return priceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Price is not found");
                    throw new EntityNotFoundException(Constants.PRICE_NOT_FOUND);
                });
    }

    private List<PriceDTO> getPriceDTOs(List<Price> prices) {
        List<PriceDTO> slots = new ArrayList<>();
        prices.forEach(slot -> slots.add(new PriceDTO(slot)));
        Collections.sort(slots);
        return slots;
    }

    private void checkDateValidity(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isBefore(LocalDateTime.now()))
            throw new InvalidDateException(Constants.INVALID_START_DATE);
        if (startDate.isAfter(endDate))
            throw new InvalidDateException(Constants.INVALID_DATE_RANGE);
    }
}
