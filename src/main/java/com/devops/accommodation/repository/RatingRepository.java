package com.devops.accommodation.repository;

import ftn.devops.db.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByAccommodation_Id(Long id);

    List<Rating> findByHost_Id(Long id);

    Rating findByGuest_IdAndAccommodation_Id(Long id, Long id1);

    Rating findByGuest_IdAndHost_Id(Long id, Long id1);


}
