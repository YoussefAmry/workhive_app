package com.workhive.projectservice.controller;

import com.workhive.projectservice.domain.dto.ProjectDto;
import com.workhive.projectservice.domain.dto.ProjectRequest;
import com.workhive.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectRequest request) {
        log.info("REST request to create project: {}", request.getName());
        ProjectDto createdProject = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        log.info("REST request to get project: {}", id);
        ProjectDto project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
    
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        log.info("REST request to get all projects");
        List<ProjectDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByManager(@PathVariable Long managerId) {
        log.info("REST request to get projects by manager: {}", managerId);
        List<ProjectDto> projects = projectService.getProjectsByManager(managerId);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByTeam(@PathVariable Long teamId) {
        log.info("REST request to get projects by team: {}", teamId);
        List<ProjectDto> projects = projectService.getProjectsByTeam(teamId);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDto>> searchProjectsByName(@RequestParam String name) {
        log.info("REST request to search projects by name: {}", name);
        List<ProjectDto> projects = projectService.searchProjectsByName(name);
        return ResponseEntity.ok(projects);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        log.info("REST request to update project: {}", id);
        ProjectDto updatedProject = projectService.updateProject(id, request);
        return ResponseEntity.ok(updatedProject);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("REST request to delete project: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
