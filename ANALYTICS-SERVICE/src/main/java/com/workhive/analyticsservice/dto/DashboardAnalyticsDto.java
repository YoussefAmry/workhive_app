package com.workhive.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsDto {
    
    // User Statistics
    private Integer totalUsers;
    private Integer activeUsers;
    
    // Project Statistics
    private Integer totalProjects;
    private Integer completedProjects;
    private Integer inProgressProjects;
    
    // Team Statistics
    private Integer totalTeams;
    private Integer averageTeamSize;
    
    // Activity Statistics
    private Integer totalActivities;
    private Integer todayActivities;
    private Integer weekActivities;
    
    // Computed Metrics
    private Double projectCompletionRate;
    private Double averageProjectsPerTeam;
}
