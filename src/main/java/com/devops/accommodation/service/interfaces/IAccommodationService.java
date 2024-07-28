package com.devops.accommodation.service.interfaces;

import ftn.devops.dto.request.CreateAccommodation;
import ftn.devops.dto.response.AccommodationDTO;

import java.io.IOException;
import java.util.zip.DataFormatException;

public interface IAccommodationService {
    AccommodationDTO addAccommodation(CreateAccommodation accommodationDTO);

    AccommodationDTO getAccommodation(long id) throws DataFormatException, IOException;
}
