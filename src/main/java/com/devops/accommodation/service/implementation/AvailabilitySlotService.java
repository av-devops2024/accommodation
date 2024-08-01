package com.devops.accommodation.service.implementation;

import com.devops.accommodation.exception.EntityNotFoundException;
import com.devops.accommodation.exception.InvalidDateException;
import com.devops.accommodation.exception.InvalidRelationshipException;
import com.devops.accommodation.repository.AvailabilitySlotRepository;
import com.devops.accommodation.service.interfaces.IAccommodationService;
import com.devops.accommodation.service.interfaces.IAvailabilitySlotService;
import com.devops.accommodation.service.interfaces.IHostService;
import com.devops.accommodation.utils.Constants;
import ftn.devops.db.Accommodation;
import ftn.devops.db.AvailabilitySlot;
import ftn.devops.db.Host;
import ftn.devops.dto.AvailabilitySlotDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilitySlotService implements IAvailabilitySlotService {
    @Autowired
    private IAccommodationService accommodationService;
    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;
    @Autowired
    private IHostService hostService;

    @Override
    public List<AvailabilitySlotDTO> addAvailabilitySlot(long accommodationId, AvailabilitySlotDTO availabilitySlotDTO) {
        checkDateValidity(availabilitySlotDTO);

        Host host = hostService.findById(1L);
        List<Accommodation> result = host.getAccommodations().stream().filter(accommodation -> accommodation.getId() == accommodationId).collect(Collectors.toList());
        if (result.size() != 1)
            throw new InvalidRelationshipException(Constants.INVALID_ACCOMMODATION_HOST_RELATIONSHIP);
        Accommodation accommodation = result.get(0);
        List<AvailabilitySlot> availabilitySlots = availabilitySlotRepository
                .findByAccommodation_IdAndValidAndEndDateGreaterThanOrderByStartDateAsc(accommodationId, true, LocalDateTime.now());

        availabilitySlots = insertNewAvailabilitySlot(availabilitySlotDTO, accommodation, availabilitySlots);
        return getAvailabilitySlotDTOs(availabilitySlots);
    }

    private List<AvailabilitySlot> insertNewAvailabilitySlot(AvailabilitySlotDTO availabilitySlotDTO, Accommodation accommodation, List<AvailabilitySlot> availabilitySlots) {
        for (AvailabilitySlot availabilitySlot : availabilitySlots){
            boolean startInbetween = isStartDateInbetween(availabilitySlotDTO, availabilitySlot);
            boolean endInbetween = isEndDateInbetween(availabilitySlotDTO, availabilitySlot);
            if (startInbetween && endInbetween)
                return availabilitySlots;
            if (startInbetween){
                availabilitySlot.setEndDate(availabilitySlotDTO.getStartDate());
                availabilitySlotRepository.save(availabilitySlot);
                break;
            }
            if (endInbetween) {
                availabilitySlot.setStartDate(availabilitySlotDTO.getEndDate());
                availabilitySlotRepository.save(availabilitySlot);
                break;
            }
        }
        AvailabilitySlot newAvailabilitySlot = new AvailabilitySlot(
                availabilitySlotDTO.getStartDate(),
                availabilitySlotDTO.getEndDate(),
                accommodation
        );
        availabilitySlotRepository.save(newAvailabilitySlot);
        availabilitySlots.add(newAvailabilitySlot);
        return availabilitySlots;
    }

    @Override
    public List<AvailabilitySlotDTO> getAvailabilitySlots(long accommodationId) {
        List<AvailabilitySlot> availabilitySlots = availabilitySlotRepository.findByAccommodation_IdAndValidOrderByStartDateAsc(accommodationId, true);
        return getAvailabilitySlotDTOs(availabilitySlots);
    }

    @Override
    public List<AvailabilitySlotDTO> getActiveAvailabilitySlots(long accommodationId) {
        List<AvailabilitySlot> availabilitySlots = availabilitySlotRepository
                .findByAccommodation_IdAndValidAndEndDateGreaterThanOrderByStartDateAsc(accommodationId, true, LocalDateTime.now());
        return getAvailabilitySlotDTOs(availabilitySlots);
    }

    @Override
    public List<AvailabilitySlotDTO> deleteAvailabilitySlot(long accommodationId, AvailabilitySlotDTO availabilitySlotDTO) {
        AvailabilitySlot availabilitySlot = availabilitySlotRepository
                .findByAccommodation_IdAndValidAndStartDateAndEndDate(accommodationId, true, availabilitySlotDTO.getStartDate(), availabilitySlotDTO.getEndDate());
        availabilitySlot.setValid(false);
        availabilitySlotRepository.save(availabilitySlot);
        return getAvailabilitySlots(accommodationId);
    }

    @Override
    public List<AvailabilitySlotDTO> deleteAvailabilitySlot(long accommodationId, long availabilitySlotId) {
        AvailabilitySlot availabilitySlot = availabilitySlotRepository.findById(availabilitySlotId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(Constants.AVAILABILITY_SLOT_NOT_FOUND));;
        if (accommodationId != availabilitySlot.getAccommodation().getId())
            throw new EntityNotFoundException(Constants.INVALID_AVAILABILITY_SLOT_ACCOMMODATION_RELATIONSHIP);
        availabilitySlot.setValid(false);
        availabilitySlotRepository.save(availabilitySlot);
        return getAvailabilitySlots(accommodationId);
    }

    private List<AvailabilitySlotDTO> getAvailabilitySlotDTOs(List<AvailabilitySlot> availabilitySlots) {
        List<AvailabilitySlotDTO> slots = new ArrayList<>();
        availabilitySlots.forEach(slot -> slots.add(new AvailabilitySlotDTO(slot)));
        Collections.sort(slots);
        return slots;
    }

    private void checkDateValidity(AvailabilitySlotDTO availabilitySlotDTO) {
        if (availabilitySlotDTO.getStartDate().isBefore(LocalDateTime.now()))
            throw new InvalidDateException(Constants.INVALID_START_DATE);
        if (availabilitySlotDTO.getStartDate().isAfter(availabilitySlotDTO.getEndDate()))
            throw new InvalidDateException(Constants.INVALID_DATE_RANGE);
    }

    private boolean isStartDateInbetween(AvailabilitySlotDTO availabilitySlotDTO, AvailabilitySlot availabilitySlot) {
        return (availabilitySlot.getStartDate().isBefore(availabilitySlotDTO.getStartDate())
                && availabilitySlot.getEndDate().isAfter(availabilitySlotDTO.getStartDate()));
    }

    private boolean isEndDateInbetween(AvailabilitySlotDTO availabilitySlotDTO, AvailabilitySlot availabilitySlot) {
        return (availabilitySlot.getStartDate().isBefore(availabilitySlotDTO.getEndDate())
                && availabilitySlot.getEndDate().isAfter(availabilitySlotDTO.getEndDate()));
    }
}
