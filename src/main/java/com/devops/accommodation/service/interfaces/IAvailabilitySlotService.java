package com.devops.accommodation.service.interfaces;

import com.devops.accommodation.dto.request.AvailabilitySlotRequest;
import ftn.devops.db.User;
import ftn.devops.dto.AvailabilitySlotDTO;

import java.util.List;

public interface IAvailabilitySlotService {
    List<AvailabilitySlotDTO>  addAvailabilitySlot(User user, long accommodationId, AvailabilitySlotRequest availabilitySlotRequest);

    List<AvailabilitySlotDTO> getAvailabilitySlots(long accommodationId);

    List<AvailabilitySlotDTO> getActiveAvailabilitySlots(long accommodationId);

    List<AvailabilitySlotDTO> deleteAvailabilitySlot(long accommodationId, AvailabilitySlotDTO availabilitySlotDTO);

    List<AvailabilitySlotDTO> deleteAvailabilitySlot(long accommodationId, long availabilitySlotId);
}
