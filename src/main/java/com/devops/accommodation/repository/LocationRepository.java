package com.devops.accommodation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ftn.devops.db.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
