package com.workhive.projectservice.service;

import com.workhive.projectservice.domain.dto.ProjectDto;
import com.workhive.projectservice.domain.dto.ProjectRequest;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(ProjectRequest request);
    ProjectDto getProjectById(Long id);
    List<ProjectDto> getAllProjects();
    List<ProjectDto> getProjectsByManager(Long managerId);
    List<ProjectDto> getProjectsByTeam(Long teamId);
    ProjectDto updateProject(Long id, ProjectRequest request);
    void deleteProject(Long id);
    List<ProjectDto> searchProjectsByName(String name);
}
