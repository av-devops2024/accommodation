package com.devops.accommodation.service.implementation;

import com.devops.accommodation.aspect.TrackExecutionTime;
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
import ftn.devops.log.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PriceService implements IPriceService {
    @Autowired
    private LogClientService logClientService;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private IReservationService reservationService;
    @Autowired
    private IAccommodationService accommodationService;

    @Override
    @TrackExecutionTime
    public List<PriceDTO> addPrice(int accommodationId, PriceDTO priceDTO) {
        logClientService.sendLog(LogType.INFO, "Add price", new Object[]{accommodationId, priceDTO});
        this.checkDateValidity(priceDTO);

        if (reservationService.hasApprovedReservation(priceDTO.getStartDate(), priceDTO.getEndDate())){
            logClientService.sendLog(LogType.WARN, "Reservation is present in period", new Object[]{accommodationId, priceDTO});
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);
        }
        checkPriceRangeOverlapping(accommodationId, priceDTO);

        Price price = createPrice(priceDTO, accommodationId);
        priceRepository.save(price);
        logClientService.sendLog(LogType.INFO, "Price is added", new Object[]{priceDTO});
        return getActivePrices(accommodationId);
    }

    @Override
    @TrackExecutionTime
    public List<PriceDTO> updatePrice(int accommodationId, PriceDTO priceDTO) {
        logClientService.sendLog(LogType.INFO, "Update price", new Object[]{accommodationId, priceDTO});
        this.checkDateValidity(priceDTO);

        if (reservationService.hasApprovedReservation(priceDTO.getStartDate(), priceDTO.getEndDate())) {
            logClientService.sendLog(LogType.WARN, "Reservation is present in period", new Object[]{accommodationId, priceDTO});
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);
        }

        Price price = getPrice(priceDTO.getId());
        if (!price.getStartDate().equals(priceDTO.getStartDate()) || !price.getEndDate().equals(priceDTO.getEndDate())) {
            logClientService.sendLog(LogType.WARN, "Price range can not be changed", new Object[]{accommodationId, priceDTO});
            throw new ActionNotAllowedException(Constants.PRICE_START_AND_END_DATE_ARE_NOT_CHANGEABLE);
        }

        price.setValue(priceDTO.getValue());
        price.setType(PriceType.valueOf(priceDTO.getType()));
        priceRepository.save(price);
        logClientService.sendLog(LogType.INFO, "Price is updated", new Object[]{priceDTO});
        return getPrices(accommodationId);
    }

    @Override
    @TrackExecutionTime
    public List<PriceDTO> getPrices(long accommodationId) {
        logClientService.sendLog(LogType.INFO, "Get prices", accommodationId);
        List<Price> prices = priceRepository.findByAccommodation_IdAndDeletedFalseOrderByStartDateAsc(accommodationId);
        return getPriceDTOs(prices);
    }

    @Override
    @TrackExecutionTime
    public List<PriceDTO> getActivePrices(long accommodationId) {
        logClientService.sendLog(LogType.INFO, "Get active prices", accommodationId);
        List<Price> prices = priceRepository.findByAccommodation_IdAndDeletedFalseAndEndDateGreaterThanOrderByStartDateAsc(accommodationId,  LocalDateTime.now());
        return getPriceDTOs(prices);
    }

    @Override
    @TrackExecutionTime
    public List<PriceDTO> deletePrices(int accommodationId, PriceDTO priceDTO) {
        Price price = getPrice(priceDTO.getId());
        if (accommodationId != price.getAccommodation().getId()){
            logClientService.sendLog(LogType.WARN, "Price does not belong to accommodation", new Object[]{accommodationId, priceDTO});
            throw new EntityNotFoundException(Constants.INVALID_PRICE_ACCOMMODATION_RELATIONSHIP);
        }
        price.setDeleted(true);
        priceRepository.save(price);
        logClientService.sendLog(LogType.INFO, "Price is deleted", new Object[]{priceDTO});
        return getPrices(accommodationId);
    }

    private Price createPrice(PriceDTO priceDTO, long accommodationId) {
        Price price = new Price();
        price.setDeleted(false);
        price.setEndDate(priceDTO.getEndDate());
        price.setStartDate(priceDTO.getStartDate());
        price.setType(PriceType.valueOf(priceDTO.getType()));
        price.setValue(priceDTO.getValue());
        price.setAccommodation(accommodationService.getAccommodationById(accommodationId));
        return price;
    }

    private void checkPriceRangeOverlapping(long accommodationId, PriceDTO priceDTO) {
        List<PriceDTO> prices = getActivePrices(accommodationId);
        List<DateRangeDTO> dateRanges = new ArrayList<>();
        prices.forEach(price -> dateRanges.add(new DateRangeDTO(price.getStartDate(), price.getEndDate())));
        dateRanges.add(new DateRangeDTO(priceDTO.getStartDate(), priceDTO.getEndDate()));
        if (DateUtils.checkIfContainsOverlapping(dateRanges)){
            logClientService.sendLog(LogType.WARN, "Price ranges are overlapping", new Object[]{accommodationId, priceDTO});
            throw new InvalidDateException(Constants.OVERLAPPING_INTERVALS);
        }
    }

    private Price getPrice(long id){
        return priceRepository.findById(id)
                .orElseThrow(() -> {
                    logClientService.sendLog(LogType.WARN, "Price is not found", id);
                    throw new EntityNotFoundException(Constants.PRICE_NOT_FOUND);
                });
    }

    private List<PriceDTO> getPriceDTOs(List<Price> prices) {
        List<PriceDTO> slots = new ArrayList<>();
        prices.forEach(slot -> slots.add(new PriceDTO(slot)));
        Collections.sort(slots);
        return slots;
    }

    private void checkDateValidity(PriceDTO priceDTO) {
        if (priceDTO.getStartDate().isBefore(LocalDateTime.now()))
            throw new InvalidDateException(Constants.INVALID_START_DATE);
        if (priceDTO.getStartDate().isAfter(priceDTO.getEndDate()))
            throw new InvalidDateException(Constants.INVALID_DATE_RANGE);
    }
}
