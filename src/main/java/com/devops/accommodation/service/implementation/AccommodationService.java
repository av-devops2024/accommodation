package com.devops.accommodation.service.implementation;

import com.devops.accommodation.exception.EntityNotFoundException;
import com.devops.accommodation.exception.InvalidImageException;
import com.devops.accommodation.repository.AccommodationRepository;
import com.devops.accommodation.service.interfaces.*;
import com.devops.accommodation.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.devops.db.*;
import ftn.devops.dto.request.CreateAccommodationRequest;
import ftn.devops.dto.request.LocationRequest;
import ftn.devops.dto.request.SearchRequest;
import ftn.devops.dto.response.AccommodationDTO;
import ftn.devops.dto.response.AccommodationResultResponse;
import ftn.devops.enums.AccommodationBenefits;
import ftn.devops.log.LogType;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private IReservationService reservationService;

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

    @Override
    public List<AccommodationDTO> searchAccommodation(SearchRequest searchRequest) {
        List<Accommodation> accommodations = filterAccommodationByGuestNumber(searchRequest.getNumberOfGuests());
        accommodations = filterAccommodationByLocation(accommodations, searchRequest.getLocationRequest());
        accommodations = filterAccommodationByAvailability(accommodations, searchRequest.getStartDate(), searchRequest.getEndDate());
        return getListOfAccommodationDTO(accommodations)
                .stream().map(x->
                        new AccommodationResultResponse(x, reservationService.countPrice(x.getId(),
                                searchRequest.getStartDate(), searchRequest.getEndDate(), searchRequest.getNumberOfGuests()))
                ).collect(Collectors.toList());
    }

    private List<Accommodation> filterAccommodationByLocation(List<Accommodation> accommodations, LocationRequest locationRequest) {
        List<Accommodation> result = new ArrayList<>();
        accommodations.forEach(acc -> {
            if (nearLocation(acc, locationRequest))
                result.add(acc);
        });
        return result;
    }

    private boolean nearLocation(Accommodation accommodation, LocationRequest locationRequest) {
        Location location = accommodation.getLocation();
        return location != null && Math.pow((locationRequest.getLatitude() - location.getLatitude()), 2) + Math.pow((locationRequest.getLongitude() - location.getLongitude()), 2) < 100;
    }

    private List<Accommodation> filterAccommodationByAvailability(List<Accommodation> accommodations, LocalDateTime startDate, LocalDateTime endDate) {
        List<Accommodation> result = new ArrayList<>();
        accommodations.forEach(acc -> {
            if (hasAvailability(acc, startDate, endDate))
                result.add(acc);
        });
        return result;
    }

    private boolean hasAvailability(Accommodation acc, LocalDateTime startDate, LocalDateTime endDate) {
        boolean hasAvailability = false;
        // is available
        for (AvailabilitySlot availabilitySlot: acc.getAvailabilitySlotList()){
            if ((availabilitySlot.getStartDate().isBefore(startDate) || availabilitySlot.getStartDate().isEqual(startDate)) &&
                    (availabilitySlot.getEndDate().isAfter(endDate) || availabilitySlot.getEndDate().isEqual(endDate))) {
                hasAvailability = true;
                break;
            }
        }
        // has no reservation
        return hasAvailability && !reservationService.hasApprovedReservationIntersect(acc.getId(), startDate, endDate);
    }

    private List<Accommodation> filterAccommodationByGuestNumber(int numberOfGuests) {
        return accommodationRepository.findByMaxNumberOfGuestsGreaterThanEqualAndMinNumberOfGuestsLessThanEqual(numberOfGuests, numberOfGuests);
    }

    private List<AccommodationDTO> getListOfAccommodationDTO(List<Accommodation> accommodations) {
        List<AccommodationDTO> accommodationDTOList = new ArrayList<>();
        for(Accommodation accommodation : accommodations){

            accommodationDTOList.add(new AccommodationDTO(accommodation));
        }

        return accommodationDTOList;
    }
}
