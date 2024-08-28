package com.devops.accommodation.service.interfaces;

import com.devops.accommodation.dto.request.PriceRequest;
import ftn.devops.db.Price;
import ftn.devops.dto.PriceDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IPriceService {
    PriceDTO addPrice(Long accommodationId, PriceRequest priceRequest);
    List<PriceDTO> updatePrice(Long accommodationId, PriceDTO priceDTO);
    List<PriceDTO> deletePrices(Long accommodationId, PriceDTO priceDTO);

    List<PriceDTO> getPrices(Long accommodationId);
    List<PriceDTO> getActivePrices(Long accommodationId);

    Price findByInterval(long accommodationId, LocalDateTime startDate, LocalDateTime endDate);
}
