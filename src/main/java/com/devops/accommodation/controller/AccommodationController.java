package com.devops.accommodation.controller;

import com.devops.accommodation.dto.response.AccommodationSearchResponse;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import ftn.devops.db.User;
import ftn.devops.dto.request.CreateAccommodationRequest;
import ftn.devops.dto.request.SearchRequest;
import ftn.devops.dto.response.AccommodationDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/accommodation")
public class AccommodationController {
    private static final Logger logger = LoggerFactory.getLogger(AccommodationController.class);
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private IAccommodationService accommodationService;
    @Autowired
    private IUserService userService;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    public AccommodationDTO addAccommodation(HttpServletRequest request,
                                             @Valid @ModelAttribute CreateAccommodationRequest accommodation) throws JsonProcessingException {
        logger.info("Add accommodation {} ", applicationName);
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
        logger.info("Get accommodation by id {} ", id);
        return accommodationService.getAccommodation(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<AccommodationDTO> getAccommodation(HttpServletRequest request) throws DataFormatException, IOException {
        User user = userService.getUser(request);
        logger.info("Add accommodations for host {}", user.getId());
        return accommodationService.getAccommodations(user.getId());
    }

    @PostMapping("search")
    @ResponseStatus(HttpStatus.OK)
    public List<AccommodationSearchResponse> searchAccommodation(@RequestBody SearchRequest searchRequest) {
        logger.info("Search accommodation {}", applicationName);
        return accommodationService.searchAccommodation(searchRequest);
    }
}
