package com.workhive.projectservice.mapper;

import com.workhive.projectservice.domain.dto.ProjectDto;
import com.workhive.projectservice.domain.dto.ProjectRequest;
import com.workhive.projectservice.domain.entity.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {
    
    private final TaskMapper taskMapper;
    
    public ProjectMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }
    
    public Project toEntity(ProjectRequest request) {
        return Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .teamId(request.getTeamId())
                .build();
    }
    
    public ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdBy(project.getCreatedBy())
                .teamId(project.getTeamId())
                .tasks(project.getTasks().stream()
                        .map(taskMapper::toDto)
                        .collect(Collectors.toList()))
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
    
    public void updateEntity(Project project, ProjectRequest request) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setTeamId(request.getTeamId());
        // Note: createdBy should not be updated after creation
    }
}
