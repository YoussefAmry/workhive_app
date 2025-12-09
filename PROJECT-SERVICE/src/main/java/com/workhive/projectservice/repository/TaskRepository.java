package com.workhive.projectservice.repository;

import com.workhive.projectservice.domain.entity.Task;
import com.workhive.projectservice.domain.entity.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByStatus(TaskStatus status);
    
    @Query("SELECT t FROM Task t JOIN t.developerIds d WHERE d = :developerId")
    List<Task> findByDeveloperId(@Param("developerId") Long developerId);
    
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
}
