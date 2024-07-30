package com.devops.accommodation.controller;

import com.devops.accommodation.service.interfaces.IBenefitsService;
import ftn.devops.enums.AccommodationBenefits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/benefits")
public class BenefitsController {

    @Autowired
    private IBenefitsService benefitsService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<AccommodationBenefits> getBenefits(){
        return benefitsService.getBenefits();
    }
}
