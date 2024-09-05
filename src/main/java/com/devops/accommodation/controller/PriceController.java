package com.devops.accommodation.controller;

import com.devops.accommodation.dto.request.PriceRequest;
import com.devops.accommodation.service.interfaces.IPriceService;
import ftn.devops.dto.PriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price")
public class PriceController {
    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

    @Autowired
    private IPriceService priceService;

    @PostMapping("/add/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public PriceDTO addPrice(@PathVariable Long accommodationId, @RequestBody PriceRequest priceRequest){
        logger.info("Add price {}", accommodationId);
        return priceService.addPrice(accommodationId, priceRequest);
    }

    @PostMapping("/update/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> updatePrice(@PathVariable Long accommodationId, @RequestBody PriceDTO priceDTO){
        logger.info("Update price {}", accommodationId);
        return priceService.updatePrice(accommodationId, priceDTO);
    }

    @GetMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> getPrice(@PathVariable Long accommodationId){
        logger.info("Get price {}", accommodationId);
        return priceService.getPrices(accommodationId);
    }

    @GetMapping("active/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> getActiveAvailabilitySlots(@PathVariable long accommodationId){
        logger.info("Get active prices {}", accommodationId);
        return priceService.getActivePrices(accommodationId);
    }
    @DeleteMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> deleteAvailabilitySlot(@PathVariable Long accommodationId,@RequestBody PriceDTO priceDTO) {
        logger.info("Delete price {}", accommodationId);
        return priceService.deletePrices(accommodationId, priceDTO);
    }
}
