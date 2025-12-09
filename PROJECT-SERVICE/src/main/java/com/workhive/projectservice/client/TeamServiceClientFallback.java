package com.workhive.projectservice.client;

import com.workhive.projectservice.client.dto.TeamDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TeamServiceClientFallback implements TeamServiceClient {
    
    @Override
    public TeamDto getTeamById(Long id) {
        log.warn("Fallback: getTeamById called for id: {}", id);
        return TeamDto.builder()
                .id(id)
                .name("Unknown Team")
                .description("Team service unavailable")
                .build();
    }
}
