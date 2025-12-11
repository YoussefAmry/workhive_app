package com.workhive.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAnalyticsDto {
    private Integer totalProjects;
    private Integer completedProjects;
    private Integer inProgressProjects;
    private Integer pendingProjects;
    private Double completionRate;
}
