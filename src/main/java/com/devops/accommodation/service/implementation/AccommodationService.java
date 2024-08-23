package com.devops.accommodation.service.implementation;

import com.devops.accommodation.exception.EntityNotFoundException;
import com.devops.accommodation.exception.InvalidImageException;
import com.devops.accommodation.repository.AccommodationRepository;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IImageService;
import com.devops.accommodation.service.interfaces.ILocationService;
import com.devops.accommodation.service.interfaces.IUserService;
import com.devops.accommodation.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.devops.db.Accommodation;
import ftn.devops.db.Image;
import ftn.devops.db.Location;
import ftn.devops.db.User;
import ftn.devops.dto.request.CreateAccommodationRequest;
import ftn.devops.dto.request.LocationRequest;
import ftn.devops.dto.response.AccommodationDTO;
import ftn.devops.enums.AccommodationBenefits;
import ftn.devops.log.LogType;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
@Transactional
public class AccommodationService implements IAccommodationService {

    @Autowired
    private LogClientService logClientService;
    @Autowired
    private AccommodationRepository accommodationRepository;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IImageService imageService;
    @Autowired
    private IUserService userService;

    @Override
    public AccommodationDTO addAccommodation(CreateAccommodationRequest accommodationDTO, User user){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            LocationRequest locationRequest = objectMapper.readValue(accommodationDTO.getLocation(), LocationRequest.class);
            Location location = locationService.saveLocation(new Location(locationRequest));
//            logClientService.sendLog(LogType.INFO, "Create accommodation", accommodationDTO);
            Accommodation accommodation = new Accommodation();
            accommodation.setName(accommodationDTO.getName());
            accommodation.setLocation(location);
            accommodationDTO.getServices().forEach(service -> accommodation.addBenefits(AccommodationBenefits.valueOf(service)));
            accommodation.setMinNumberOfGuests(accommodationDTO.getMinNumberOfGuests());
            accommodation.setMaxNumberOfGuests(accommodationDTO.getMaxNumberOfGuests());
            accommodation.setAutomaticallyAcceptRequest(accommodationDTO.isAutomaticallyAcceptRequest());
            accommodation.setHost(user);

            for (MultipartFile image : accommodationDTO.getImages()) {
                try {
                        Image resultedImage = imageService.addImage(image);
                        resultedImage.setAccommodation(accommodation);
                        accommodation.addImage(resultedImage);
                } catch (IOException e) {
                    logClientService.sendLog(LogType.WARN, "Invalid image", image);
                    throw new InvalidImageException(Constants.IMAGE_INVALID + image.getOriginalFilename());
                }
            }
            accommodationRepository.save(accommodation);
//            accommodation.getImages().forEach(image -> this.imageService.decompressImage(image));
//            logClientService.sendLog(LogType.INFO, "Added accommodation", accommodationDTO);
            return new AccommodationDTO(accommodation);
            }catch (JsonProcessingException e){
                throw new RuntimeException(e.getMessage());
            }
    }

    @Override
    public AccommodationDTO getAccommodation(long id) {
        logClientService.sendLog(LogType.INFO, "Get accommodation", id);
        Accommodation accommodation = getAccommodationById(id);
        Hibernate.initialize(accommodation.getBenefits());
        Hibernate.initialize(accommodation.getImages());
        accommodation.getImages().forEach(image -> this.imageService.decompressImage(image));
        logClientService.sendLog(LogType.INFO, "Found accommodation", id);
        return new AccommodationDTO(accommodation);
    }

    @Override
    public Accommodation getAccommodationById(long id) {
        return this.accommodationRepository.findById(id)
                .orElseThrow(() -> {
                    logClientService.sendLog(LogType.WARN, "Accommodation not found", id);
                    throw new EntityNotFoundException(Constants.ACCOMMODATION_NOT_FOUND);
                });
    }

    @Override
    public List<AccommodationDTO> getAccommodations(Long userId) {
        return getListOfAccommodationDTO(this.accommodationRepository.findAllByHostId(userId));
    }

    private List<AccommodationDTO> getListOfAccommodationDTO(List<Accommodation> accommodations) {
        List<AccommodationDTO> accommodationDTOList = new ArrayList<>();
        for(Accommodation accommodation : accommodations){

            accommodationDTOList.add(new AccommodationDTO(accommodation));
        }

        return accommodationDTOList;
    }
}
