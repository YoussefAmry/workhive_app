package com.workhive.notificationservice.service;

import com.workhive.notificationservice.client.UserServiceClient;
import com.workhive.notificationservice.dto.UserDto;
import com.workhive.notificationservice.entity.Notification;
import com.workhive.notificationservice.repository.NotificationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    @CircuitBreaker(name = "notificationService", fallbackMethod = "sendNotificationFallback")
    @Retry(name = "notificationService")
    public Notification sendNotification(Long userId, String message, String type) {
        log.info("Sending notification to user: {}", userId);

        // Validate user exists by calling USER-SERVICE
        UserDto user = userServiceClient.getUserById(userId);
        log.info("Validated user: {} - {}", user.getUsername(), user.getEmail());

        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type != null ? type : "INFO")
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification sent successfully with id: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        return notificationRepository.findByUserIdAndIsRead(userId, false);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    // Fallback method
    private Notification sendNotificationFallback(Long userId, String message, String type, Exception e) {
        log.error("Fallback: sendNotification failed for user: {}", userId, e);
        // Still save notification even if user service is down
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type != null ? type : "INFO")
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }
}
