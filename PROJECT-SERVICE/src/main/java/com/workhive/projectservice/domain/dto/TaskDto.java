package com.workhive.projectservice.domain.dto;

import com.workhive.projectservice.domain.entity.Task.TaskStatus;
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
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long projectId;
    private String projectName; // Populated from Project
    
    @Builder.Default
    private List<Long> developerIds = new ArrayList<>();
    
    private List<String> developerNames; // Populated from USER-SERVICE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
