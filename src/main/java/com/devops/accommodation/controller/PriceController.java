package com.devops.accommodation.controller;

import com.devops.accommodation.service.interfaces.IPriceService;
import ftn.devops.dto.PriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price")
public class PriceController {

    @Autowired
    private IPriceService priceService;

    @PostMapping("/add/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> addPrice(@PathVariable int accommodationId, @RequestBody PriceDTO priceDTO){
        return priceService.addPrice(accommodationId, priceDTO);
    }

    @PostMapping("/update/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> updatePrice(@PathVariable int accommodationId, @RequestBody PriceDTO priceDTO){
        return priceService.updatePrice(accommodationId, priceDTO);
    }

    @GetMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> getPrice(@PathVariable long accommodationId){
        return priceService.getPrices(accommodationId);
    }

    @GetMapping("active/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> getActiveAvailabilitySlots(@PathVariable long accommodationId){
        return priceService.getActivePrices(accommodationId);
    }
    @DeleteMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> deleteAvailabilitySlot(@PathVariable int accommodationId,@RequestBody PriceDTO priceDTO) {
        return priceService.deletePrices(accommodationId, priceDTO);
    }
}
