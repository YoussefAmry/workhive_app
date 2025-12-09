package com.workhive.projectservice.service.impl;

import com.workhive.projectservice.client.TeamServiceClient;
import com.workhive.projectservice.client.UserServiceClient;
import com.workhive.projectservice.client.dto.TeamDto;
import com.workhive.projectservice.client.dto.UserDto;
import com.workhive.projectservice.domain.dto.ProjectDto;
import com.workhive.projectservice.domain.dto.ProjectRequest;
import com.workhive.projectservice.domain.entity.Project;
import com.workhive.projectservice.exception.ResourceNotFoundException;
import com.workhive.projectservice.mapper.ProjectMapper;
import com.workhive.projectservice.repository.ProjectRepository;
import com.workhive.projectservice.service.ProjectService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserServiceClient userServiceClient;
    private final TeamServiceClient teamServiceClient;
    
    @Override
    @Transactional
    @CircuitBreaker(name = "projectService", fallbackMethod = "createProjectFallback")
    @Retry(name = "projectService")
    public ProjectDto createProject(ProjectRequest request) {
        log.info("Creating project: {}", request.getName());
        Project project = projectMapper.toEntity(request);
        Project savedProject = projectRepository.save(project);
        return enrichProjectDto(projectMapper.toDto(savedProject));
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "projectService", fallbackMethod = "getProjectFallback")
    @Retry(name = "projectService")
    public ProjectDto getProjectById(Long id) {
        log.info("Fetching project by id: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return enrichProjectDto(projectMapper.toDto(project));
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "projectService", fallbackMethod = "getAllProjectsFallback")
    public List<ProjectDto> getAllProjects() {
        log.info("Fetching all projects");
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .map(this::enrichProjectDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "projectService", fallbackMethod = "getProjectsByManagerFallback")
    public List<ProjectDto> getProjectsByManager(Long managerId) {
        log.info("Fetching projects by manager: {}", managerId);
        return projectRepository.findByCreatedBy(managerId).stream()
                .map(projectMapper::toDto)
                .map(this::enrichProjectDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "projectService", fallbackMethod = "getProjectsByTeamFallback")
    public List<ProjectDto> getProjectsByTeam(Long teamId) {
        log.info("Fetching projects by team: {}", teamId);
        return projectRepository.findByTeamId(teamId).stream()
                .map(projectMapper::toDto)
                .map(this::enrichProjectDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    @CircuitBreaker(name = "projectService", fallbackMethod = "updateProjectFallback")
    @Retry(name = "projectService")
    public ProjectDto updateProject(Long id, ProjectRequest request) {
        log.info("Updating project: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        projectMapper.updateEntity(project, request);
        Project updatedProject = projectRepository.save(project);
        return enrichProjectDto(projectMapper.toDto(updatedProject));
    }
    
    @Override
    @Transactional
    public void deleteProject(Long id) {
        log.info("Deleting project: {}", id);
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "projectService", fallbackMethod = "searchProjectsFallback")
    public List<ProjectDto> searchProjectsByName(String name) {
        log.info("Searching projects by name: {}", name);
        return projectRepository.findByNameContainingIgnoreCase(name).stream()
                .map(projectMapper::toDto)
                .map(this::enrichProjectDto)
                .collect(Collectors.toList());
    }
    
    private ProjectDto enrichProjectDto(ProjectDto projectDto) {
        try {
            // Fetch manager information
            UserDto manager = userServiceClient.getUserById(projectDto.getCreatedBy());
            projectDto.setManagerName(manager.getFullName());
            
            // Fetch team information if teamId is present
            if (projectDto.getTeamId() != null) {
                TeamDto team = teamServiceClient.getTeamById(projectDto.getTeamId());
                projectDto.setTeamName(team.getName());
            }
        } catch (Exception e) {
            log.error("Error enriching project DTO: {}", e.getMessage());
        }
        return projectDto;
    }
    
    // Fallback methods
    private ProjectDto createProjectFallback(ProjectRequest request, Exception e) {
        log.error("Fallback: createProject failed", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
    
    private ProjectDto getProjectFallback(Long id, Exception e) {
        log.error("Fallback: getProjectById failed for id: {}", id, e);
        throw new ResourceNotFoundException("Project service unavailable");
    }
    
    private List<ProjectDto> getAllProjectsFallback(Exception e) {
        log.error("Fallback: getAllProjects failed", e);
        return List.of();
    }
    
    private List<ProjectDto> getProjectsByManagerFallback(Long managerId, Exception e) {
        log.error("Fallback: getProjectsByManager failed for manager: {}", managerId, e);
        return List.of();
    }
    
    private List<ProjectDto> getProjectsByTeamFallback(Long teamId, Exception e) {
        log.error("Fallback: getProjectsByTeam failed for team: {}", teamId, e);
        return List.of();
    }
    
    private ProjectDto updateProjectFallback(Long id, ProjectRequest request, Exception e) {
        log.error("Fallback: updateProject failed for id: {}", id, e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
    
    private List<ProjectDto> searchProjectsFallback(String name, Exception e) {
        log.error("Fallback: searchProjectsByName failed for name: {}", name, e);
        return List.of();
    }
}
