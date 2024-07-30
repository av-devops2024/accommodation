package com.devops.accommodation.service.interfaces;

import ftn.devops.enums.AccommodationBenefits;

import java.util.List;

public interface IBenefitsService {
    List<AccommodationBenefits> getBenefits();
}
