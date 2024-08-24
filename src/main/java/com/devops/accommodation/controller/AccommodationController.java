package com.devops.accommodation.controller;

import com.devops.accommodation.service.implementation.UserService;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import ftn.devops.db.User;
import ftn.devops.dto.request.CreateAccommodationRequest;
import ftn.devops.dto.response.AccommodationDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Array;
import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/accommodation")
public class AccommodationController {

    @Autowired
    private IAccommodationService accommodationService;
    @Autowired
    private IUserService userService;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    public AccommodationDTO addAccommodation(HttpServletRequest request,
                                             @Valid @ModelAttribute CreateAccommodationRequest accommodation) throws JsonProcessingException {
        try {
            User user = userService.getUser(request);
            return accommodationService.addAccommodation(accommodation, user);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccommodationDTO getAccommodation(@PathVariable long id) throws DataFormatException, IOException {
        return accommodationService.getAccommodation(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<AccommodationDTO> getAccommodation(HttpServletRequest request) throws DataFormatException, IOException {
        User user = userService.getUser(request);
        return accommodationService.getAccommodations(user.getId());
    }
}
