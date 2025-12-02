package com.workhive.teamservice.service;

import com.workhive.teamservice.client.UserClient;
import com.workhive.teamservice.domain.dto.TeamRequest;
import com.workhive.teamservice.domain.dto.TeamResponse;
import com.workhive.teamservice.domain.dto.UserDTO;
import com.workhive.teamservice.domain.entity.Team;
import com.workhive.teamservice.mapper.TeamMapper;
import com.workhive.teamservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserClient userClient;
    private final TeamMapper teamMapper;

    @Override
    public TeamResponse createTeam(TeamRequest request) {

        // ✅ Verify Manager
        UserDTO manager = userClient.getUserById(request.getManagerId());
        if (!"MANAGER".equals(manager.getRole()))
            throw new RuntimeException("User is not a manager");

        // ✅ Verify Developers
        for (Long devId : request.getDeveloperIds()) {
            UserDTO dev = userClient.getUserById(devId);
            if (!"DEVELOPER".equals(dev.getRole()))
                throw new RuntimeException("User " + devId + " is not a developer");
        }

        Team team = teamMapper.toEntity(request);
        Team saved = teamRepository.save(team);

        return teamMapper.toResponse(saved);
    }

    @Override
    public TeamResponse getById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(TeamNotFoundException::new);

        return teamMapper.toResponse(team);
    }
}

