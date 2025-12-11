package com.workhive.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamAnalyticsDto {
    private Integer totalTeams;
    private Integer averageTeamSize;
    private Double teamsPerManager;
}
