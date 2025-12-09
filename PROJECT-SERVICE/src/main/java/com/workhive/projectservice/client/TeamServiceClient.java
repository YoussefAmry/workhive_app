package com.workhive.projectservice.client;

import com.workhive.projectservice.client.dto.TeamDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "TEAM-SERVICE", fallback = TeamServiceClientFallback.class)
public interface TeamServiceClient {
    
    @GetMapping("/api/teams/{id}")
    TeamDto getTeamById(@PathVariable("id") Long id);
}
