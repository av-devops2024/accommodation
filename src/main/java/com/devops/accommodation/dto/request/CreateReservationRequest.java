package com.devops.accommodation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int numberOfGuests;
    private long accommodationId;
}
