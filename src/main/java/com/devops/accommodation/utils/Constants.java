package com.devops.accommodation.utils;

public class Constants {

    public static final String HOST_NOT_FOUND = "Host is not found";
    public static final String ACCOMMODATION_NOT_FOUND = "Accommodation is not found";
    public static final String IMAGE_INVALID = "Image is not valid, with the name: ";

    public static final String AVAILABILITY_SLOT_NOT_FOUND = "Availability slot is not found";
    public static final String INVALID_START_DATE = "Start date should be in the future";
    public static final String INVALID_DATE_RANGE = "Start date should be before end date";

    public static final String INVALID_ACCOMMODATION_HOST_RELATIONSHIP = "Accommodation does not belong to host";
    public static final String INVALID_AVAILABILITY_SLOT_ACCOMMODATION_RELATIONSHIP = "Availability slot does not belong to accommodation";

    public static final String PRICE_NOT_FOUND = "Price is not found";
    public static final String INVALID_PRICE_ACCOMMODATION_RELATIONSHIP = "Price does not belong to accommodation";
    public static final String OVERLAPPING_INTERVALS = "Action is not allowed because intervals are overlapping";
    public static final String PRICE_START_AND_END_DATE_ARE_NOT_CHANGEABLE = "Only price value and type can be changed";

    public static final String ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION = "This action is not allowed because the specified date range contains reservation";

}
