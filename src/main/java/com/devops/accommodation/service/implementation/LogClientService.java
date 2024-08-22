package com.devops.accommodation.service.implementation;

import com.google.gson.Gson;
import ftn.devops.LogServiceGrpc;
import ftn.devops.LoggingProto;
import ftn.devops.db.Accommodation;
import ftn.devops.db.Reservation;
import ftn.devops.db.User;
import ftn.devops.enums.NotificationType;
import ftn.devops.log.LogType;
import ftn.devops.log.ServiceType;
import ftn.devops.utils.DateUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    // TODO: add/update logs everywhere !!!
    public boolean sendLog(LogType level, String message, Object param) {
        LoggingProto.LogRequest request = LoggingProto.LogRequest.newBuilder()
                .setLevel(level.toString())
                .setTimestamp(DateUtils.TimestampToString(LocalDateTime.now()))
                .setService(ServiceType.ACCOMMODATION.toString())
                .setMessage(message)
                .setParameter(gson.toJson(param))
                .setTimestamp(LocalDateTime.now().toString())
                .build();
        LoggingProto.Response response = logServiceStub.sendLog(request);
        return response.getSuccess();
    }
    public boolean sendLog(LogType level, String message, List<Object> param) {
        LoggingProto.LogRequest request = LoggingProto.LogRequest.newBuilder()
                .setLevel(level.toString())
                .setTimestamp(DateUtils.TimestampToString(LocalDateTime.now()))
                .setService(ServiceType.ACCOMMODATION.toString())
                .setMessage(message)
                .setParameter(gson.toJson(param))
                .setTimestamp(LocalDateTime.now().toString())
                .build();
        LoggingProto.Response response = logServiceStub.sendLog(request);
        return response.getSuccess();
    }

    public boolean sendTrace(String className, String methodName, int duration, String params) {
        LoggingProto.TraceRequest request = LoggingProto.TraceRequest.newBuilder()
                .setService(ServiceType.ACCOMMODATION.toString())
                .setClassName(className)
                .setMethodName(methodName)
                .setDuration(duration)
                .setParameter(params)
                .setTimestamp(DateUtils.TimestampToString(LocalDateTime.now()))
                .build();
        LoggingProto.Response response = logServiceStub.sendTrace(request);
        return response.getSuccess();
    }

    public boolean sendNotification(User user, Accommodation accommodation, Reservation reservation, NotificationType notificationType) {
        LoggingProto.NotificationObject request = LoggingProto.NotificationObject.newBuilder()
                .setReceiverId(user.getId())
                .setGuestName(reservation.getGuest().getName())
                .setAccommodationId(accommodation.getId())
                .setAccommodationName(accommodation.getName())
                .setStartDate(DateUtils.TimestampToString(reservation.getStartDate()))
                .setEndDate(DateUtils.TimestampToString(reservation.getEndDate()))
                .setNotificationType(notificationType.toString())
                .setTimestamp(DateUtils.TimestampToString(LocalDateTime.now()))
                .build();
        LoggingProto.Response response = logServiceStub.sendNotification(request);
        return response.getSuccess();
    }
}
