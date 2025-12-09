package com.workhive.projectservice.domain.dto;

import com.workhive.projectservice.domain.entity.Task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Status is required")
    private TaskStatus status;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    @Builder.Default
    private List<Long> developerIds = new ArrayList<>();
}
