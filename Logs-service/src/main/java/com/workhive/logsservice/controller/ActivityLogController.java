package com.workhive.logsservice.controller;

import com.workhive.logsservice.dto.ActivityLogDto;
import com.workhive.logsservice.dto.CreateActivityLogInput;
import com.workhive.logsservice.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @PostMapping
    public ResponseEntity<ActivityLogDto> createActivityLog(@Valid @RequestBody CreateActivityLogInput input) {
        ActivityLogDto created = activityLogService.createActivityLog(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLogDto>> getUserActivityLogs(@PathVariable Long userId) {
        List<ActivityLogDto> logs = activityLogService.getByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<ActivityLogDto>> getRecentUserActivities(@PathVariable Long userId) {
        List<ActivityLogDto> logs = activityLogService.getRecentUserActivities(userId);
        return ResponseEntity.ok(logs);
    }
}
