package com.workhive.teamservice.service;

import com.workhive.teamservice.client.UserClient;
import com.workhive.teamservice.domain.dto.TeamRequest;
import com.workhive.teamservice.domain.dto.TeamResponse;
import com.workhive.teamservice.domain.dto.UserDTO;
import com.workhive.teamservice.domain.entity.Team;
import com.workhive.teamservice.exception.TeamNotFoundException;
import com.workhive.teamservice.mapper.TeamMapper;
import com.workhive.teamservice.repository.TeamRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserClient userClient;
    private final TeamMapper teamMapper;

    @Override
    @Transactional
    @CircuitBreaker(name = "teamService", fallbackMethod = "createTeamFallback")
    @Retry(name = "teamService")
    public TeamResponse createTeam(TeamRequest request) {
        log.info("Creating team: {}", request.getName());

        // ✅ Verify Manager
        UserDTO manager = userClient.getUserById(request.getManagerId());
        if (!"MANAGER".equals(manager.getRole()))
            throw new RuntimeException("User is not a manager");

        // ✅ Verify Developers (optional)
        if (request.getDeveloperIds() != null && !request.getDeveloperIds().isEmpty()) {
            for (Long devId : request.getDeveloperIds()) {
                UserDTO dev = userClient.getUserById(devId);
                if (!"DEVELOPER".equals(dev.getRole()))
                    throw new RuntimeException("User " + devId + " is not a developer");
            }
        }

        Team team = teamMapper.toEntity(request);
        Team saved = teamRepository.save(team);

        return enrichTeamResponse(teamMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "teamService", fallbackMethod = "getByIdFallback")
    @Retry(name = "teamService")
    public TeamResponse getById(Long id) {
        log.info("Fetching team by id: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + id));

        return enrichTeamResponse(teamMapper.toResponse(team));
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "teamService", fallbackMethod = "getAllTeamsFallback")
    public List<TeamResponse> getAllTeams() {
        log.info("Fetching all teams");
        return teamRepository.findAll().stream()
                .map(teamMapper::toResponse)
                .map(this::enrichTeamResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "teamService", fallbackMethod = "updateTeamFallback")
    @Retry(name = "teamService")
    public TeamResponse updateTeam(Long id, TeamRequest request) {
        log.info("Updating team: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + id));

        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setDeveloperIds(request.getDeveloperIds());
        
        Team updated = teamRepository.save(team);
        return enrichTeamResponse(teamMapper.toResponse(updated));
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        log.info("Deleting team: {}", id);
        if (!teamRepository.existsById(id)) {
            throw new TeamNotFoundException("Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }

    private TeamResponse enrichTeamResponse(TeamResponse response) {
        try {
            // Fetch manager name
            UserDTO manager = userClient.getUserById(response.getManagerId());
            response.setManagerName(manager.getFullName());

            // Fetch developer names
            if (response.getDeveloperIds() != null && !response.getDeveloperIds().isEmpty()) {
                List<String> devNames = response.getDeveloperIds().stream()
                        .map(devId -> {
                            try {
                                UserDTO dev = userClient.getUserById(devId);
                                return dev.getFullName();
                            } catch (Exception e) {
                                return "Unknown Developer";
                            }
                        })
                        .collect(Collectors.toList());
                response.setDeveloperNames(devNames);
            }
        } catch (Exception e) {
            log.error("Error enriching team response: {}", e.getMessage());
        }
        return response;
    }

    // Fallback methods
    private TeamResponse createTeamFallback(TeamRequest request, Exception e) {
        log.error("Fallback: createTeam failed", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private TeamResponse getByIdFallback(Long id, Exception e) {
        log.error("Fallback: getById failed for id: {}", id, e);
        throw new TeamNotFoundException("Team service unavailable");
    }

    private List<TeamResponse> getAllTeamsFallback(Exception e) {
        log.error("Fallback: getAllTeams failed", e);
        return List.of();
    }

    private TeamResponse updateTeamFallback(Long id, TeamRequest request, Exception e) {
        log.error("Fallback: updateTeam failed for id: {}", id, e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
}

