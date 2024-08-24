package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.LocationRepository;
import com.devops.accommodation.service.interfaces.ILocationService;
import ftn.devops.db.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService implements ILocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Override
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }
}
