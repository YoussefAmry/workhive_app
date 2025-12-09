package com.workhive.logsservice.service;

import com.workhive.logsservice.client.UserServiceClient;
import com.workhive.logsservice.entity.ActivityLog;
import com.workhive.logsservice.dto.ActivityLogDto;
import com.workhive.logsservice.dto.CreateActivityLogInput;
import com.workhive.logsservice.dto.UserDto;
import com.workhive.logsservice.repository.ActivityLogRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    @CircuitBreaker(name = "activityLogService", fallbackMethod = "createActivityLogFallback")
    @Retry(name = "activityLogService")
    public ActivityLogDto createActivityLog(CreateActivityLogInput input) {
        log.info("Creating activity log for user: {}", input.getUsername());

        ActivityLog activityLog = ActivityLog.builder()
                .userId(input.getUserId())
                .username(input.getUsername())
                .action(input.getAction())
                .entityType(input.getEntityType())
                .entityId(input.getEntityId())
                .details(input.getDetails())
                .ipAddress(input.getIpAddress())
                .userAgent(input.getUserAgent())
                .timestamp(LocalDateTime.now())
                .build();

        ActivityLog saved = activityLogRepository.save(activityLog);
        return enrichWithUserData(saved);
    }

    @Transactional(readOnly = true)
    public ActivityLogDto getById(Long id) {
        log.info("Fetching activity log by id: {}", id);
        ActivityLog activityLog = activityLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity log not found with id: " + id));
        return enrichWithUserData(activityLog);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getAllActivityLogs() {
        log.info("Fetching all recent activity logs");
        return activityLogRepository.findTop50ByOrderByTimestampDesc().stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getByUserId(Long userId) {
        log.info("Fetching activity logs for user id: {}", userId);
        return activityLogRepository.findByUserId(userId).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getByUsername(String username) {
        log.info("Fetching activity logs for username: {}", username);
        return activityLogRepository.findByUsername(username).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getByAction(String action) {
        log.info("Fetching activity logs for action: {}", action);
        return activityLogRepository.findByAction(action).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getByEntityType(String entityType) {
        log.info("Fetching activity logs for entity type: {}", entityType);
        return activityLogRepository.findByEntityType(entityType).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getByEntity(String entityType, Long entityId) {
        log.info("Fetching activity logs for entity: {} with id: {}", entityType, entityId);
        return activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching activity logs between {} and {}", startDate, endDate);
        return activityLogRepository.findByTimestampBetween(startDate, endDate).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching activity logs for user {} between {} and {}", userId, startDate, endDate);
        return activityLogRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDto> getRecentUserActivities(Long userId) {
        log.info("Fetching recent activities for user: {}", userId);
        return activityLogRepository.findTop50ByUserIdOrderByTimestampDesc(userId).stream()
                .map(this::enrichWithUserData)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteActivityLog(Long id) {
        log.info("Deleting activity log: {}", id);
        if (activityLogRepository.existsById(id)) {
            activityLogRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteUserActivityLogs(Long userId) {
        log.info("Deleting all activity logs for user: {}", userId);
        List<ActivityLog> userLogs = activityLogRepository.findByUserId(userId);
        activityLogRepository.deleteAll(userLogs);
        return true;
    }

    @CircuitBreaker(name = "activityLogService", fallbackMethod = "enrichWithUserDataFallback")
    @Retry(name = "activityLogService")
    private ActivityLogDto enrichWithUserData(ActivityLog activityLog) {
        UserDto user = userServiceClient.getUserById(activityLog.getUserId());

        return ActivityLogDto.builder()
                .id(activityLog.getId())
                .userId(activityLog.getUserId())
                .username(activityLog.getUsername())
                .userFullName(user != null ? user.getFullName() : activityLog.getUsername())
                .action(activityLog.getAction())
                .entityType(activityLog.getEntityType())
                .entityId(activityLog.getEntityId())
                .details(activityLog.getDetails())
                .timestamp(activityLog.getTimestamp())
                .ipAddress(activityLog.getIpAddress())
                .userAgent(activityLog.getUserAgent())
                .build();
    }

    // Fallback methods
    private ActivityLogDto createActivityLogFallback(CreateActivityLogInput input, Exception e) {
        log.error("Fallback: createActivityLog failed", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private ActivityLogDto enrichWithUserDataFallback(ActivityLog activityLog, Exception e) {
        log.warn("Fallback: enrichWithUserData failed for activity log id: {}", activityLog.getId());
        return ActivityLogDto.builder()
                .id(activityLog.getId())
                .userId(activityLog.getUserId())
                .username(activityLog.getUsername())
                .userFullName(activityLog.getUsername())
                .action(activityLog.getAction())
                .entityType(activityLog.getEntityType())
                .entityId(activityLog.getEntityId())
                .details(activityLog.getDetails())
                .timestamp(activityLog.getTimestamp())
                .ipAddress(activityLog.getIpAddress())
                .userAgent(activityLog.getUserAgent())
                .build();
    }
}
