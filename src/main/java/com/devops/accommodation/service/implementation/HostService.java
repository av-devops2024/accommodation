package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.HostRepository;
import com.devops.accommodation.service.interfaces.IHostService;
import ftn.devops.db.Host;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HostService implements IHostService {
    @Autowired
    private HostRepository hostRepository;

    @Override
    public Host findById(long id) {
        return hostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(""));
    }
}
