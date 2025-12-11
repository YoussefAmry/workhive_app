package com.workhive.analyticsservice.service;

import com.workhive.analyticsservice.dto.*;
import com.workhive.analyticsservice.grpc.logs.*;
import com.workhive.analyticsservice.grpc.project.*;
import com.workhive.analyticsservice.grpc.team.*;
import com.workhive.analyticsservice.grpc.user.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    @GrpcClient("USER-SERVICE")
    private UserStatisticsServiceGrpc.UserStatisticsServiceBlockingStub userServiceStub;

    @GrpcClient("PROJECT-SERVICE")
    private ProjectStatisticsServiceGrpc.ProjectStatisticsServiceBlockingStub projectServiceStub;

    @GrpcClient("TEAM-SERVICE")
    private TeamStatisticsServiceGrpc.TeamStatisticsServiceBlockingStub teamServiceStub;

    @GrpcClient("LOGS-SERVICE")
    private LogsStatisticsServiceGrpc.LogsStatisticsServiceBlockingStub logsServiceStub;

    @CircuitBreaker(name = "analyticsService", fallbackMethod = "getDashboardFallback")
    @Retry(name = "analyticsService")
    public DashboardAnalyticsDto getDashboardAnalytics() {
        log.info("Fetching dashboard analytics via gRPC");

        // Call all services via gRPC
        UserCountResponse userStats = getUserStatistics();
        ProjectCountResponse projectStats = getProjectStatistics();
        TeamCountResponse teamStats = getTeamStatistics();
        ActivityCountResponse activityStats = getActivityStatistics();

        // Calculate computed metrics
        double completionRate = projectStats.getTotalProjects() > 0
                ? (double) projectStats.getCompletedProjects() / projectStats.getTotalProjects() * 100
                : 0.0;

        double avgProjectsPerTeam = teamStats.getTotalTeams() > 0
                ? (double) projectStats.getTotalProjects() / teamStats.getTotalTeams()
                : 0.0;

        return DashboardAnalyticsDto.builder()
                .totalUsers(userStats.getTotalUsers())
                .activeUsers(userStats.getActiveUsers())
                .totalProjects(projectStats.getTotalProjects())
                .completedProjects(projectStats.getCompletedProjects())
                .inProgressProjects(projectStats.getInProgressProjects())
                .totalTeams(teamStats.getTotalTeams())
                .averageTeamSize(teamStats.getAverageTeamSize())
                .totalActivities(activityStats.getTotalActivities())
                .todayActivities(activityStats.getTodayActivities())
                .weekActivities(activityStats.getWeekActivities())
                .projectCompletionRate(completionRate)
                .averageProjectsPerTeam(avgProjectsPerTeam)
                .build();
    }

    @CircuitBreaker(name = "analyticsService", fallbackMethod = "getUserAnalyticsFallback")
    @Retry(name = "analyticsService")
    public UserAnalyticsDto getUserAnalytics() {
        log.info("Fetching user analytics via gRPC");
        UserCountResponse response = getUserStatistics();

        return UserAnalyticsDto.builder()
                .totalUsers(response.getTotalUsers())
                .activeUsers(response.getActiveUsers())
                .inactiveUsers(response.getTotalUsers() - response.getActiveUsers())
                .build();
    }

    @CircuitBreaker(name = "analyticsService", fallbackMethod = "getProjectAnalyticsFallback")
    @Retry(name = "analyticsService")
    public ProjectAnalyticsDto getProjectAnalytics() {
        log.info("Fetching project analytics via gRPC");
        ProjectCountResponse response = getProjectStatistics();

        double completionRate = response.getTotalProjects() > 0
                ? (double) response.getCompletedProjects() / response.getTotalProjects() * 100
                : 0.0;

        int pendingProjects = response.getTotalProjects() - response.getCompletedProjects() - response.getInProgressProjects();

        return ProjectAnalyticsDto.builder()
                .totalProjects(response.getTotalProjects())
                .completedProjects(response.getCompletedProjects())
                .inProgressProjects(response.getInProgressProjects())
                .pendingProjects(pendingProjects)
                .completionRate(completionRate)
                .build();
    }

    @CircuitBreaker(name = "analyticsService", fallbackMethod = "getTeamAnalyticsFallback")
    @Retry(name = "analyticsService")
    public TeamAnalyticsDto getTeamAnalytics() {
        log.info("Fetching team analytics via gRPC");
        TeamCountResponse response = getTeamStatistics();

        return TeamAnalyticsDto.builder()
                .totalTeams(response.getTotalTeams())
                .averageTeamSize(response.getAverageTeamSize())
                .teamsPerManager(1.0) // Simplified - each team has one manager
                .build();
    }

    // Private helper methods for gRPC calls
    private UserCountResponse getUserStatistics() {
        try {
            return userServiceStub.getUserCount(UserCountRequest.newBuilder().build());
        } catch (Exception e) {
            log.error("Error calling USER-SERVICE via gRPC", e);
            return UserCountResponse.newBuilder().setTotalUsers(0).setActiveUsers(0).build();
        }
    }

    private ProjectCountResponse getProjectStatistics() {
        try {
            return projectServiceStub.getProjectCount(ProjectCountRequest.newBuilder().build());
        } catch (Exception e) {
            log.error("Error calling PROJECT-SERVICE via gRPC", e);
            return ProjectCountResponse.newBuilder()
                    .setTotalProjects(0)
                    .setCompletedProjects(0)
                    .setInProgressProjects(0)
                    .build();
        }
    }

    private TeamCountResponse getTeamStatistics() {
        try {
            return teamServiceStub.getTeamCount(TeamCountRequest.newBuilder().build());
        } catch (Exception e) {
            log.error("Error calling TEAM-SERVICE via gRPC", e);
            return TeamCountResponse.newBuilder().setTotalTeams(0).setAverageTeamSize(0).build();
        }
    }

    private ActivityCountResponse getActivityStatistics() {
        try {
            return logsServiceStub.getActivityCount(ActivityCountRequest.newBuilder().build());
        } catch (Exception e) {
            log.error("Error calling LOGS-SERVICE via gRPC", e);
            return ActivityCountResponse.newBuilder()
                    .setTotalActivities(0)
                    .setTodayActivities(0)
                    .setWeekActivities(0)
                    .build();
        }
    }

    // Fallback methods
    private DashboardAnalyticsDto getDashboardFallback(Exception e) {
        log.error("Fallback: getDashboardAnalytics failed", e);
        return DashboardAnalyticsDto.builder()
                .totalUsers(0).activeUsers(0)
                .totalProjects(0).completedProjects(0).inProgressProjects(0)
                .totalTeams(0).averageTeamSize(0)
                .totalActivities(0).todayActivities(0).weekActivities(0)
                .projectCompletionRate(0.0).averageProjectsPerTeam(0.0)
                .build();
    }

    private UserAnalyticsDto getUserAnalyticsFallback(Exception e) {
        log.error("Fallback: getUserAnalytics failed", e);
        return UserAnalyticsDto.builder()
                .totalUsers(0).activeUsers(0).inactiveUsers(0)
                .build();
    }

    private ProjectAnalyticsDto getProjectAnalyticsFallback(Exception e) {
        log.error("Fallback: getProjectAnalytics failed", e);
        return ProjectAnalyticsDto.builder()
                .totalProjects(0).completedProjects(0)
                .inProgressProjects(0).pendingProjects(0)
                .completionRate(0.0)
                .build();
    }

    private TeamAnalyticsDto getTeamAnalyticsFallback(Exception e) {
        log.error("Fallback: getTeamAnalytics failed", e);
        return TeamAnalyticsDto.builder()
                .totalTeams(0).averageTeamSize(0).teamsPerManager(0.0)
                .build();
    }
}
