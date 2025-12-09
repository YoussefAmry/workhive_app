package com.workhive.teamservice.service;

import com.workhive.teamservice.domain.dto.TeamRequest;
import com.workhive.teamservice.domain.dto.TeamResponse;

import java.util.List;

public interface TeamService {
    TeamResponse createTeam(TeamRequest request);
    TeamResponse getById(Long id);
    List<TeamResponse> getAllTeams();
    TeamResponse updateTeam(Long id, TeamRequest request);
    void deleteTeam(Long id);
}
