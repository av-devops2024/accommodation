package com.devops.accommodation.service.interfaces;

import ftn.devops.dto.AvailabilitySlotDTO;

import java.util.List;

public interface IAvailabilitySlotService {
    List<AvailabilitySlotDTO>  addAvailabilitySlot(long accommodationId, AvailabilitySlotDTO availabilitySlotDTO);

    List<AvailabilitySlotDTO> getAvailabilitySlots(long accommodationId);

    List<AvailabilitySlotDTO> getActiveAvailabilitySlots(long accommodationId);

    List<AvailabilitySlotDTO> deleteAvailabilitySlot(long accommodationId, AvailabilitySlotDTO availabilitySlotDTO);

    List<AvailabilitySlotDTO> deleteAvailabilitySlot(long accommodationId, long availabilitySlotId);
}
