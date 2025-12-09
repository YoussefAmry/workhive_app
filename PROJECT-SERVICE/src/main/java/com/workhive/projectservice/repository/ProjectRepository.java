package com.workhive.projectservice.repository;

import com.workhive.projectservice.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedBy(Long managerId);
    List<Project> findByTeamId(Long teamId);
    List<Project> findByNameContainingIgnoreCase(String name);
}
