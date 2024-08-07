package com.devops.accommodation.service.implementation;

import com.devops.accommodation.service.interfaces.IBenefitsService;
import ftn.devops.enums.AccommodationBenefits;
import ftn.devops.log.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BenefitsService implements IBenefitsService {
    @Autowired
    private LogClientService logClientService;
    @Override
    public List<AccommodationBenefits> getBenefits() {
        logClientService.sendLog(LogType.INFO, "Get benefits", null);
        return Arrays.asList(AccommodationBenefits.class.getEnumConstants());
    }
}
