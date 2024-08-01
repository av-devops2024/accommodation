package com.devops.accommodation.controller;

import com.devops.accommodation.service.interfaces.IAvailabilitySlotService;
import ftn.devops.dto.AvailabilitySlotDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availabilitySlot")
public class AvailabilitySlotController {

    @Autowired
    private IAvailabilitySlotService availabilitySlotService;

    @PostMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> addAvailabilitySlot(@PathVariable int accommodationId, @RequestBody AvailabilitySlotDTO availabilitySlotDTO){
        return availabilitySlotService.addAvailabilitySlot(accommodationId, availabilitySlotDTO);
    }

    @GetMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> getAvailabilitySlots(@PathVariable long accommodationId){
        return availabilitySlotService.getAvailabilitySlots(accommodationId);
    }

    @GetMapping("active/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> getActiveAvailabilitySlots(@PathVariable long accommodationId){
        return availabilitySlotService.getActiveAvailabilitySlots(accommodationId);
    }
    @DeleteMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> deleteAvailabilitySlot(@PathVariable int accommodationId, @RequestBody AvailabilitySlotDTO availabilitySlotDTO) {
        return availabilitySlotService.deleteAvailabilitySlot(accommodationId, availabilitySlotDTO);
    }

    @DeleteMapping("{accommodationId}/{availabilitySlotId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> deleteAvailabilitySlot(@PathVariable int accommodationId, @PathVariable long availabilitySlotId){
        return availabilitySlotService.deleteAvailabilitySlot(accommodationId, availabilitySlotId);
    }
}
