package com.devops.accommodation.service.implementation;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PriceService implements IPriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private IReservationService reservationService;
    @Autowired
    private IAccommodationService accommodationService;

    @Override
    public List<PriceDTO> addPrice(int accommodationId, PriceDTO priceDTO) {
        this.checkDateValidity(priceDTO);

        if (reservationService.hasApprovedReservation(priceDTO.getStartDate(), priceDTO.getEndDate()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);

        checkPriceRangeOverlapping(accommodationId, priceDTO);

        Price price = createPrice(priceDTO, accommodationId);
        priceRepository.save(price);
        return getActivePrices(accommodationId);
    }

    @Override
    public List<PriceDTO> updatePrice(int accommodationId, PriceDTO priceDTO) {
        this.checkDateValidity(priceDTO);

        if (reservationService.hasApprovedReservation(priceDTO.getStartDate(), priceDTO.getEndDate()))
            throw new ActionNotAllowedException(Constants.ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION);

        Price price = priceRepository.findById(priceDTO.getId())
                .orElseThrow(() -> new ActionNotAllowedException(Constants.PRICE_NOT_FOUND));
        if (!price.getStartDate().equals(priceDTO.getStartDate()) || !price.getEndDate().equals(priceDTO.getEndDate()))
            throw new ActionNotAllowedException(Constants.PRICE_START_AND_END_DATE_ARE_NOT_CHANGEABLE);

        price.setValue(priceDTO.getValue());
        price.setType(PriceType.valueOf(priceDTO.getType()));
        priceRepository.save(price);
        return getPrices(accommodationId);
    }

    @Override
    public List<PriceDTO> getPrices(long accommodationId) {
        List<Price> prices = priceRepository.findByAccommodation_IdAndDeletedFalseOrderByStartDateAsc(accommodationId);
        return getPriceDTOs(prices);
    }

    @Override
    public List<PriceDTO> getActivePrices(long accommodationId) {
        List<Price> prices = priceRepository.findByAccommodation_IdAndDeletedFalseAndEndDateGreaterThanOrderByStartDateAsc(accommodationId,  LocalDateTime.now());
        return getPriceDTOs(prices);
    }

    @Override
    public List<PriceDTO> deletePrices(int accommodationId, PriceDTO priceDTO) {
        Price price = priceRepository.findById(priceDTO.getId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(Constants.PRICE_NOT_FOUND));
        if (accommodationId != price.getAccommodation().getId())
            throw new EntityNotFoundException(Constants.INVALID_PRICE_ACCOMMODATION_RELATIONSHIP);
        price.setDeleted(true);
        priceRepository.save(price);
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

    private List<PriceDTO> checkPriceRangeOverlapping(long accommodationId, PriceDTO priceDTO) {
        List<PriceDTO> prices = getActivePrices(accommodationId);
        List<DateRangeDTO> dateRanges = new ArrayList<>();
        prices.forEach(price -> dateRanges.add(new DateRangeDTO(price.getStartDate(), price.getEndDate())));
        dateRanges.add(new DateRangeDTO(priceDTO.getStartDate(), priceDTO.getEndDate()));
        if (DateUtils.checkIfContainsOverlapping(dateRanges))
            throw new InvalidDateException(Constants.OVERLAPPING_INTERVALS);
        return prices;
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
