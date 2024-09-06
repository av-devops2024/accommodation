package com.devops.accommodation.service.implementation;

import com.devops.accommodation.dto.response.AccommodationSearchResponse;
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
import ftn.devops.dto.response.ImageDTO;
import ftn.devops.dto.response.LocationDTO;
import ftn.devops.enums.AccommodationBenefits;
import ftn.devops.enums.PriceType;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AccommodationService implements IAccommodationService {

    private final Logger logger = LoggerFactory.getLogger(AccommodationService.class);
    @Autowired
    private AccommodationRepository accommodationRepository;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IImageService imageService;
    @Autowired
    private IReservationService reservationService;
    @Autowired
    private PriceService priceService;

    @Override
    public AccommodationDTO addAccommodation(CreateAccommodationRequest accommodationDTO, User user){
        logger.info("Add accommodation");
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            LocationRequest locationRequest = objectMapper.readValue(accommodationDTO.getLocation(), LocationRequest.class);
            Location location = locationService.saveLocation(new Location(locationRequest));
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
                    logger.warn("Invalid image");
                    throw new InvalidImageException(Constants.IMAGE_INVALID + image.getOriginalFilename());
                }
            }
            accommodationRepository.save(accommodation);
            return new AccommodationDTO(accommodation);
            }catch (JsonProcessingException e){
                throw new RuntimeException(e.getMessage());
            }
    }

    @Override
    public AccommodationDTO getAccommodation(long id) {
        logger.info("Get accommodation");
        Accommodation accommodation = getAccommodationById(id);
        Hibernate.initialize(accommodation.getBenefits());
        Hibernate.initialize(accommodation.getImages());
        accommodation.getImages().forEach(image -> this.imageService.decompressImage(image));
        logger.info("Found accommodation");
        return new AccommodationDTO(accommodation);
    }

    @Override
    public Accommodation getAccommodationById(long id) {
        return this.accommodationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Accommodation not found");
                    throw new EntityNotFoundException(Constants.ACCOMMODATION_NOT_FOUND);
                });
    }

    @Override
    public List<AccommodationDTO> getAccommodations(Long userId) {
        return getListOfAccommodationDTO(this.accommodationRepository.findAllByHostId(userId));
    }

    @Override
    public List<AccommodationSearchResponse> searchAccommodation(SearchRequest searchRequest) {
        List<Accommodation> accommodations = accommodationRepository.findAll();
        if (searchRequest.getNumberOfGuests() > 0)
            accommodations = filterAccommodationByGuestNumber(accommodations, searchRequest.getNumberOfGuests());
        if (searchRequest.getLocationRequest() != null)
            accommodations = filterAccommodationByLocation(accommodations, searchRequest.getLocationRequest());
        if (searchRequest.getStartDate() != null && searchRequest.getEndDate() != null) {
            accommodations = filterAccommodationByAvailability(accommodations, searchRequest.getStartDate(), searchRequest.getEndDate());
        }
        List<AccommodationSearchResponse> responseList = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            Price price = priceService.findByInterval(accommodation.getId(), searchRequest.getStartDate(), searchRequest.getEndDate());
            AccommodationSearchResponse accommodationSearchResponse = new AccommodationSearchResponse();
            accommodationSearchResponse.setId(accommodation.getId());
            accommodationSearchResponse.setName(accommodation.getName());
            accommodationSearchResponse.setBenefits(accommodation.getBenefits());
            accommodationSearchResponse.setMinNumberOfGuests(accommodation.getMinNumberOfGuests());
            accommodationSearchResponse.setMaxNumberOfGuests(accommodation.getMaxNumberOfGuests());
            accommodationSearchResponse.setLocation(new LocationDTO(accommodation.getLocation()));
            accommodationSearchResponse.setDailyPrice(price.getValue());
            accommodationSearchResponse.setPriceType(price.getType().toString());
            long numberOfNights = ChronoUnit.DAYS.between(searchRequest.getStartDate(), searchRequest.getEndDate());
            if(price.getType().equals(PriceType.PER_ACCOMMODATION)){
                accommodationSearchResponse.setTotalPrice(numberOfNights * price.getValue());
            } else {
                accommodationSearchResponse.setTotalPrice(searchRequest.getNumberOfGuests() * numberOfNights * price.getValue());
            }
            List<ImageDTO> images = new ArrayList();
            accommodation.getImages().forEach((image) -> {
                images.add(new ImageDTO(image));
            });
            accommodationSearchResponse.setImages(images);
            responseList.add(accommodationSearchResponse);
        }

        return responseList;
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

    private List<Accommodation> filterAccommodationByGuestNumber(List<Accommodation> accommodations, int numberOfGuests) {
        List<Accommodation> result = new ArrayList<>();
        accommodations.forEach(acc -> {
            if (acc.getMinNumberOfGuests() <= numberOfGuests && acc.getMaxNumberOfGuests() >= numberOfGuests)
                result.add(acc);
        });
        return result;
    }

    private List<AccommodationDTO> getListOfAccommodationDTO(List<Accommodation> accommodations) {
        List<AccommodationDTO> accommodationDTOList = new ArrayList<>();
        for(Accommodation accommodation : accommodations){

            accommodationDTOList.add(new AccommodationDTO(accommodation));
        }

        return accommodationDTOList;
    }
}
