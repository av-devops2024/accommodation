package com.devops.accommodation.service.implementation;

import com.devops.accommodation.exception.EntityNotFoundException;
import com.devops.accommodation.exception.InvalidImageException;
import com.devops.accommodation.repository.AccommodationRepository;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IImageService;
import com.devops.accommodation.utils.Constants;
import ftn.devops.db.Accommodation;
import ftn.devops.db.Image;
import ftn.devops.dto.request.CreateAccommodation;
import ftn.devops.dto.response.AccommodationDTO;
import ftn.devops.enums.AccommodationBenefits;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class AccommodationService implements IAccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private IImageService imageService;

    @Override
    public AccommodationDTO addAccommodation(CreateAccommodation accommodationDTO) {
        Accommodation accommodation = new Accommodation();
        accommodation.setName(accommodationDTO.getName());
        accommodationDTO.getServices().forEach(service -> accommodation.addBenefits(AccommodationBenefits.valueOf(service)));
        accommodation.setMinNumberOfGuests(accommodationDTO.getMinNumberOfGuests());
        accommodation.setMaxNumberOfGuests(accommodationDTO.getMaxNumberOfGuests());
        accommodation.setAutomaticallyAcceptRequest(accommodationDTO.isAutomaticallyAcceptRequest());
        for (MultipartFile image : accommodationDTO.getImages()) {
            try {
                    Image resultedImage = imageService.addImage(image);
                    resultedImage.setAccommodation(accommodation);
                    accommodation.addImage(resultedImage);
            } catch (IOException e) {
                throw new InvalidImageException(Constants.IMAGE_INVALID + image.getOriginalFilename());
            }
        }
        accommodationRepository.save(accommodation);
        accommodation.getImages().forEach(image -> this.imageService.decompressImage(image));
        return new AccommodationDTO(accommodation);
    }

    @Override
    public AccommodationDTO getAccommodation(long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> {throw new EntityNotFoundException(Constants.ACCOMMODATION_NOT_FOUND);});
        Hibernate.initialize(accommodation.getBenefits());
        Hibernate.initialize(accommodation.getImages());
        accommodation.getImages().forEach(image -> this.imageService.decompressImage(image));
        return new AccommodationDTO(accommodation);
    }

}
