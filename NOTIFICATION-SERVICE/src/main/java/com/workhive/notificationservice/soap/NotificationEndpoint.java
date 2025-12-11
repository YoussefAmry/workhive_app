package com.workhive.notificationservice.soap;

import com.workhive.notificationservice.entity.Notification;
import com.workhive.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

@Endpoint
@Slf4j
@RequiredArgsConstructor
public class NotificationEndpoint {

    private static final String NAMESPACE_URI = "http://workhive.com/notification";

    private final NotificationService notificationService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "sendNotificationRequest")
    @ResponsePayload
    public NotificationResponse sendNotification(@RequestPayload NotificationRequest request) {
        log.info("SOAP Request: sendNotification for user: {}", request.getUserId());

        try {
            Notification notification = notificationService.sendNotification(
                    request.getUserId(),
                    request.getMessage(),
                    request.getType()
            );

            return NotificationResponse.builder()
                    .id(notification.getId())
                    .userId(notification.getUserId())
                    .message(notification.getMessage())
                    .type(notification.getType())
                    .isRead(notification.getIsRead())
                    .timestamp(notification.getTimestamp().toString())
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return NotificationResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNotificationsRequest")
    @ResponsePayload
    public GetNotificationsResponse getNotifications(@RequestPayload GetNotificationsRequest request) {
        log.info("SOAP Request: getNotifications for user: {}", request.getUserId());

        List<Notification> notifications = notificationService.getNotificationsByUser(request.getUserId());

        List<NotificationResponse> responseList = notifications.stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .userId(n.getUserId())
                        .message(n.getMessage())
                        .type(n.getType())
                        .isRead(n.getIsRead())
                        .timestamp(n.getTimestamp().toString())
                        .success(true)
                        .build())
                .collect(Collectors.toList());

        return GetNotificationsResponse.builder()
                .notifications(responseList)
                .totalCount(responseList.size())
                .build();
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "markAsReadRequest")
    @ResponsePayload
    public NotificationResponse markAsRead(@RequestPayload MarkAsReadRequest request) {
        log.info("SOAP Request: markAsRead for notification: {}", request.getNotificationId());

        try {
            Notification notification = notificationService.markAsRead(request.getNotificationId());

            return NotificationResponse.builder()
                    .id(notification.getId())
                    .userId(notification.getUserId())
                    .message(notification.getMessage())
                    .type(notification.getType())
                    .isRead(notification.getIsRead())
                    .timestamp(notification.getTimestamp().toString())
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return NotificationResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
