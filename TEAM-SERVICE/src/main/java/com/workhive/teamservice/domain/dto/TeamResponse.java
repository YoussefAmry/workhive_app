package com.workhive.teamservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private Long managerId;
    private String managerName;
    
    @Builder.Default
    private List<Long> developerIds = new ArrayList<>();
    
    private List<String> developerNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

