package com.devops.accommodation.service.implementation;

import com.google.gson.Gson;
import ftn.devops.LogServiceGrpc;
import ftn.devops.LoggingProto;
import ftn.devops.db.Accommodation;
import ftn.devops.db.Reservation;
import ftn.devops.db.User;
import ftn.devops.enums.NotificationType;
import ftn.devops.utils.DateUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogClientService {

    private final LogServiceGrpc.LogServiceBlockingStub logServiceStub;
    @Autowired
    private Gson gson;

    public LogClientService(@Value("${spring.grpc.host}") String host, @Value("${spring.grpc.port}") int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        logServiceStub = LogServiceGrpc.newBlockingStub(channel);
    }

    public boolean sendNotification(User user, Accommodation accommodation, Reservation reservation, NotificationType notificationType) {
        LoggingProto.NotificationObject request = LoggingProto.NotificationObject.newBuilder()
                .setReceiverId(user.getId())
                .setGuestName(reservation != null ? reservation.getGuest().getName() : "")
                .setAccommodationId(accommodation != null ? accommodation.getId() : 0)
                .setAccommodationName(accommodation != null ? accommodation.getName() : "")
                .setStartDate(reservation != null ? DateUtils.TimestampToString(reservation.getStartDate()) : "")
                .setEndDate(reservation != null ? DateUtils.TimestampToString(reservation.getEndDate()) : "")
                .setNotificationType(notificationType.toString())
                .setTimestamp(DateUtils.TimestampToString(LocalDateTime.now()))
                .build();
        LoggingProto.Response response = logServiceStub.sendNotification(request);
        return response.getSuccess();
    }

    public boolean sendNotificationWithGuestName(User user, String guestName, Accommodation accommodation, Reservation reservation, NotificationType notificationType) {
        LoggingProto.NotificationObject request = LoggingProto.NotificationObject.newBuilder()
                .setReceiverId(user.getId())
                .setGuestName(guestName)
                .setAccommodationId(accommodation != null ? accommodation.getId() : 0)
                .setAccommodationName(accommodation != null ? accommodation.getName() : "")
                .setStartDate(reservation != null ? DateUtils.TimestampToString(reservation.getStartDate()) : "")
                .setEndDate(reservation != null ? DateUtils.TimestampToString(reservation.getEndDate()) : "")
                .setNotificationType(notificationType.toString())
                .setTimestamp(DateUtils.TimestampToString(LocalDateTime.now()))
                .build();
        LoggingProto.Response response = logServiceStub.sendNotification(request);
        return response.getSuccess();
    }
}
