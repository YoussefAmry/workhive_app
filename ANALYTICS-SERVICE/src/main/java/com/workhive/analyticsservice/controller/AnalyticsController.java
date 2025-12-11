package com.workhive.analyticsservice.controller;

import com.workhive.analyticsservice.dto.*;
import com.workhive.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardAnalyticsDto> getDashboardAnalytics() {
        log.info("REST Request: GET /api/analytics/dashboard");
        DashboardAnalyticsDto analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/users")
    public ResponseEntity<UserAnalyticsDto> getUserAnalytics() {
        log.info("REST Request: GET /api/analytics/users");
        UserAnalyticsDto analytics = analyticsService.getUserAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/projects")
    public ResponseEntity<ProjectAnalyticsDto> getProjectAnalytics() {
        log.info("REST Request: GET /api/analytics/projects");
        ProjectAnalyticsDto analytics = analyticsService.getProjectAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/teams")
    public ResponseEntity<TeamAnalyticsDto> getTeamAnalytics() {
        log.info("REST Request: GET /api/analytics/teams");
        TeamAnalyticsDto analytics = analyticsService.getTeamAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ANALYTICS-SERVICE is running and ready to aggregate data via gRPC");
    }
}
