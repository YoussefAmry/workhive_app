package com.workhive.logsservice.resolver;

import com.workhive.logsservice.dto.ActivityLogDto;
import com.workhive.logsservice.dto.CreateActivityLogInput;
import com.workhive.logsservice.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ActivityLogResolver {

    private final ActivityLogService activityLogService;

    @QueryMapping
    public ActivityLogDto activityLog(@Argument Long id) {
        return activityLogService.getById(id);
    }

    @QueryMapping
    public List<ActivityLogDto> allActivityLogs() {
        return activityLogService.getAllActivityLogs();
    }

    @QueryMapping
    public List<ActivityLogDto> activityLogsByUserId(@Argument Long userId) {
        return activityLogService.getByUserId(userId);
    }

    @QueryMapping
    public List<ActivityLogDto> activityLogsByUsername(@Argument String username) {
        return activityLogService.getByUsername(username);
    }

    @QueryMapping
    public List<ActivityLogDto> activityLogsByAction(@Argument String action) {
        return activityLogService.getByAction(action);
    }

    @QueryMapping
    public List<ActivityLogDto> activityLogsByEntityType(@Argument String entityType) {
        return activityLogService.getByEntityType(entityType);
    }

    @QueryMapping
    public List<ActivityLogDto> activityLogsByEntity(@Argument String entityType, @Argument Long entityId) {
        return activityLogService.getByEntity(entityType, entityId);
    }

    @QueryMapping
    public List<ActivityLogDto> activityLogsByDateRange(@Argument String startDate, @Argument String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return activityLogService.getByDateRange(start, end);
    }

    @QueryMapping
    public List<ActivityLogDto> activityLogsByUserAndDateRange(
            @Argument Long userId,
            @Argument String startDate,
            @Argument String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return activityLogService.getByUserAndDateRange(userId, start, end);
    }

    @QueryMapping
    public List<ActivityLogDto> recentUserActivities(@Argument Long userId) {
        return activityLogService.getRecentUserActivities(userId);
    }

    @MutationMapping
    public ActivityLogDto createActivityLog(@Argument CreateActivityLogInput input) {
        return activityLogService.createActivityLog(input);
    }

    @MutationMapping
    public Boolean deleteActivityLog(@Argument Long id) {
        return activityLogService.deleteActivityLog(id);
    }

    @MutationMapping
    public Boolean deleteUserActivityLogs(@Argument Long userId) {
        return activityLogService.deleteUserActivityLogs(userId);
    }
}
