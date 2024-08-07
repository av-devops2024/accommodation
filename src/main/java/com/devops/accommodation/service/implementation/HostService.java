package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.HostRepository;
import com.devops.accommodation.service.interfaces.IHostService;
import com.devops.accommodation.utils.Constants;
import ftn.devops.db.Host;
import ftn.devops.log.LogType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HostService implements IHostService {
    @Autowired
    private LogClientService logClientService;
    @Autowired
    private HostRepository hostRepository;

    @Override
    public Host findById(long id) {
        return hostRepository.findById(id)
                .orElseThrow(() -> {
                    logClientService.sendLog(LogType.WARN, "Host not found", id);
                    throw new EntityNotFoundException(Constants.HOST_NOT_FOUND);
                });
    }
}
