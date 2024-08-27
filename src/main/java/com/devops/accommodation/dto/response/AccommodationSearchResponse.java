package com.devops.accommodation.dto.response;

import ftn.devops.dto.response.ImageDTO;
import ftn.devops.dto.response.LocationDTO;
import ftn.devops.enums.AccommodationBenefits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationSearchResponse {
    private Long id;
    private String name;
    private LocationDTO location;
    private List<AccommodationBenefits> benefits;
    private List<ImageDTO> images;
    private int minNumberOfGuests;
    private int maxNumberOfGuests;
    private boolean automaticallyAcceptRequest;
    private String priceType;
    private double dailyPrice;
    private double totalPrice;
}
