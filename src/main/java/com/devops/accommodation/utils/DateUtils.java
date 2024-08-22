package com.devops.accommodation.utils;

import com.devops.accommodation.exception.InvalidDateException;
import ftn.devops.dto.DateRangeDTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class DateUtils {

    public static boolean checkIfContainsOverlapping(List<DateRangeDTO> dateRanges){
        Collections.sort(dateRanges);
        for (int i = 1; i < dateRanges.size(); i++){
            if (isOverlapping(dateRanges.get(i-1), dateRanges.get(i)))
                return true;
        }
        return false;
    }

    public static boolean isOverlapping(DateRangeDTO firstRange, DateRangeDTO secondRange){
        return firstRange.getFirstDate().isBefore(secondRange.getSecondDate()) &&
                secondRange.getFirstDate().isBefore(firstRange.getSecondDate());
    }

    public static void checkDateValidity(LocalDateTime firstDate, LocalDateTime secondDate) {
        if (firstDate.isBefore(LocalDateTime.now()))
            throw new InvalidDateException(Constants.INVALID_START_DATE);
        if (firstDate.isAfter(secondDate))
            throw new InvalidDateException(Constants.INVALID_DATE_RANGE);
    }
}
