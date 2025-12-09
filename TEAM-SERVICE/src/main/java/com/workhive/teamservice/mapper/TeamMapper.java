package com.workhive.teamservice.mapper;

import com.workhive.teamservice.domain.dto.TeamRequest;
import com.workhive.teamservice.domain.dto.TeamResponse;
import com.workhive.teamservice.domain.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toEntity(TeamRequest request) {
        return Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .managerId(request.getManagerId())
                .developerIds(request.getDeveloperIds())
                .build();
    }

    public TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .managerId(team.getManagerId())
                .developerIds(team.getDeveloperIds())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}
