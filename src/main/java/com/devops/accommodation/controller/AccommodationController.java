package com.devops.accommodation.controller;

import com.devops.accommodation.service.interfaces.IAccommodationService;
import ftn.devops.dto.request.CreateAccommodation;
import ftn.devops.dto.response.AccommodationDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/accommodation")
public class AccommodationController {

    @Autowired
    private IAccommodationService accommodationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public AccommodationDTO addAccommodation(@Valid @ModelAttribute CreateAccommodation accommodationDTO){
        return accommodationService.addAccommodation(accommodationDTO);
    }


    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccommodationDTO getAccommodation(@PathVariable long id) throws DataFormatException, IOException {
        return accommodationService.getAccommodation(id);
    }
}
