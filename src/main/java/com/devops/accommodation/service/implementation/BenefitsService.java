package com.devops.accommodation.service.implementation;

import com.devops.accommodation.service.interfaces.IBenefitsService;
import ftn.devops.enums.AccommodationBenefits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BenefitsService implements IBenefitsService {
    private final Logger logger = LoggerFactory.getLogger(BenefitsService.class);
    @Override
    public List<AccommodationBenefits> getBenefits() {
        logger.info("Get benefits");
        return Arrays.asList(AccommodationBenefits.class.getEnumConstants());
    }
}
