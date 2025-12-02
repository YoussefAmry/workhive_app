package com.workhive.teamservice.controller;

import com.workhive.teamservice.domain.dto.TeamRequest;
import com.workhive.teamservice.domain.dto.TeamResponse;
import com.workhive.teamservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public TeamResponse create(@RequestBody TeamRequest request) {
        return teamService.createTeam(request);
    }

    @GetMapping("/{id}")
    public TeamResponse getById(@PathVariable Long id) {
        return teamService.getById(id);
    }
}

