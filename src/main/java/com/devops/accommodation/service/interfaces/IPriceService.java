package com.devops.accommodation.service.interfaces;

import ftn.devops.db.Price;
import ftn.devops.dto.PriceDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IPriceService {
    List<PriceDTO> addPrice(int accommodationId, PriceDTO priceDTO);
    List<PriceDTO> updatePrice(int accommodationId, PriceDTO priceDTO);
    List<PriceDTO> deletePrices(int accommodationId, PriceDTO priceDTO);

    List<PriceDTO> getPrices(long accommodationId);
    List<PriceDTO> getActivePrices(long accommodationId);

    Price findByInterval(LocalDateTime startDate, LocalDateTime endDate);
}
