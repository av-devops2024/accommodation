package com.devops.accommodation.service.implementation;

import com.devops.accommodation.service.interfaces.IBenefitsService;
import ftn.devops.enums.AccommodationBenefits;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BenefitsService implements IBenefitsService {
    @Override
    public List<AccommodationBenefits> getBenefits() {
        return Arrays.asList(AccommodationBenefits.class.getEnumConstants());
    }
}
