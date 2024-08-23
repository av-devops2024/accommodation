package com.devops.accommodation.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import ftn.devops.db.Accommodation;
import ftn.devops.db.User;
import ftn.devops.dto.request.CreateAccommodationRequest;
import ftn.devops.dto.response.AccommodationDTO;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public interface IAccommodationService {
    AccommodationDTO addAccommodation(CreateAccommodationRequest accommodationDTO, User user) throws JsonProcessingException;

    AccommodationDTO getAccommodation(long id) throws DataFormatException, IOException;

    Accommodation getAccommodationById(long id);
    List<AccommodationDTO> getAccommodations(Long userId) throws DataFormatException, IOException;
}
