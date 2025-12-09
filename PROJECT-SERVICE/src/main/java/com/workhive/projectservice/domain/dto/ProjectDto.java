package com.workhive.projectservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long createdBy;
    private String managerName; // Populated from USER-SERVICE
    private Long teamId;
    private String teamName; // Populated from TEAM-SERVICE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<TaskDto> tasks = new ArrayList<>();
}
