package com.devops.accommodation.service.interfaces;

import ftn.devops.db.Host;

public interface IHostService {
    Host findById(long id);
}
