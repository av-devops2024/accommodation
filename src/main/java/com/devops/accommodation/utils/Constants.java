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
    public static final String PRICE_RANGE_NOT_FOUND = "There is no price found for this range";
    public static final String INVALID_PRICE_ACCOMMODATION_RELATIONSHIP = "Price does not belong to accommodation";
    public static final String OVERLAPPING_INTERVALS = "Action is not allowed because intervals are overlapping";
    public static final String PRICE_START_AND_END_DATE_ARE_NOT_CHANGEABLE = "Only price value and type can be changed";

    public static final String ACTION_NOT_ALLOWED_BECAUSE_CONTAINS_RESERVATION = "This action is not allowed because the specified date range contains reservation";
    public static final String RESERVATION_CAN_NOT_BE_DELETED_AFTER_IT_GOT_APPROVED = "This action is not allowed because the reservation is already approved: try cancelling it";

    public static final String NOT_EXISTING_PRICE_TYPE = "Price type does not exists";
    public static final String TOO_HIGH_GUEST_NUMBER = "Accommodation does not allow that amount of guest, too many";
    public static final String TOO_LOW_GUEST_NUMBER = "Accommodation does not allow that amount of guest, too few";

    public static final String INVALID_RESERVATION_ACCEPT = "Accommodation does not belong to user, he can't accept it";
    public static final String INVALID_RESERVATION_DELETE = "Reservation does not belong to user, he can't delete it";
    public static final String INVALID_RESERVATION_CANCEL = "Reservation does not belong to user, he can't cancel it";
    public static final String INVALID_RESERVATION_CANCEL_TIME = "Cancelling is only possible before the start of the reservation";
    public static final String RESERVATION_NOT_FOUND  = "Reservation is not found";
}
