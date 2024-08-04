package com.devops.accommodation.utils;

import ftn.devops.dto.DateRangeDTO;

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
}
