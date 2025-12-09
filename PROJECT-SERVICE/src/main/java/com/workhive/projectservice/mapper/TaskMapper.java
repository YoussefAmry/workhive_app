package com.workhive.projectservice.mapper;

import com.workhive.projectservice.domain.dto.TaskDto;
import com.workhive.projectservice.domain.dto.TaskRequest;
import com.workhive.projectservice.domain.entity.Task;
import com.workhive.projectservice.domain.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    
    public Task toEntity(TaskRequest request, Project project) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .project(project)
                .developerIds(request.getDeveloperIds())
                .build();
    }
    
    public TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .developerIds(task.getDeveloperIds())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
    
    public void updateEntity(Task task, TaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDeveloperIds(request.getDeveloperIds());
        // Note: project should not be changed after creation
    }
}
