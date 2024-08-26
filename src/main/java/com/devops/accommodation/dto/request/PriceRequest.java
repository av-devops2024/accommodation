package com.devops.accommodation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double value;
    private String type;
}
