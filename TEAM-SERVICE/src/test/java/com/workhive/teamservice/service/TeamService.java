package com.workhive.teamservice.service;

import com.workhive.teamservice.domain.dto.TeamRequest;
import com.workhive.teamservice.domain.dto.TeamResponse;

public interface TeamService {
    TeamResponse createTeam(TeamRequest request);
    TeamResponse getById(Long id);
}
