package com.workhive.teamservice.controller;

import com.workhive.teamservice.domain.dto.TeamRequest;
import com.workhive.teamservice.domain.dto.TeamResponse;
import com.workhive.teamservice.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        log.info("REST request to create team: {}", request.getName());
        TeamResponse response = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getById(@PathVariable Long id) {
        log.info("REST request to get team: {}", id);
        TeamResponse response = teamService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        log.info("REST request to get all teams");
        List<TeamResponse> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> update(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        log.info("REST request to update team: {}", id);
        TeamResponse response = teamService.updateTeam(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("REST request to delete team: {}", id);
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}

