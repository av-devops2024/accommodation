package com.devops.accommodation.controller;

import com.devops.accommodation.service.interfaces.IBenefitsService;
import ftn.devops.enums.AccommodationBenefits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/benefits")
public class BenefitsController {
    private static final Logger logger = LoggerFactory.getLogger(BenefitsController.class);
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private IBenefitsService benefitsService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<AccommodationBenefits> getBenefits(){
        logger.info("Get benefits - {}", applicationName);
        return benefitsService.getBenefits();
    }
}
