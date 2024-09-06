package com.devops.accommodation.controller;

import com.devops.accommodation.dto.request.AvailabilitySlotRequest;
import com.devops.accommodation.service.interfaces.IAvailabilitySlotService;
import com.devops.accommodation.service.interfaces.IUserService;
import ftn.devops.db.User;
import ftn.devops.dto.AvailabilitySlotDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availabilitySlot")
public class AvailabilitySlotController {
    private static final Logger logger = LoggerFactory.getLogger(AvailabilitySlotController.class);
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private IAvailabilitySlotService availabilitySlotService;

    @Autowired
    private IUserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> addAvailabilitySlot(@RequestBody AvailabilitySlotRequest availabilitySlotRequest, HttpServletRequest request){
        logger.info("Add availability slot - {}", applicationName);
        User user = userService.getUser(request);
        return availabilitySlotService.addAvailabilitySlot(user, availabilitySlotRequest.getAccommodationId(), availabilitySlotRequest);
    }

    @GetMapping("{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> getAvailabilitySlots(@PathVariable long accommodationId){
        logger.info("Get availability slots for accommodation {}", accommodationId);
        return availabilitySlotService.getAvailabilitySlots(accommodationId);
    }

    @GetMapping("active/{accommodationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> getActiveAvailabilitySlots(@PathVariable long accommodationId){
        logger.info("Get active availability slots for accommodation {}", accommodationId);
        return availabilitySlotService.getActiveAvailabilitySlots(accommodationId);
    }

    @DeleteMapping("{accommodationId}/{availabilitySlotId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilitySlotDTO> deleteAvailabilitySlot(@PathVariable int accommodationId, @PathVariable long availabilitySlotId){
        logger.info("Delete availability slot for accommodation {}", accommodationId);
        return availabilitySlotService.deleteAvailabilitySlot(accommodationId, availabilitySlotId);
    }
}
