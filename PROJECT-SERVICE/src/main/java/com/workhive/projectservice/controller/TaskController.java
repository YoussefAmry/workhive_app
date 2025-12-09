package com.workhive.projectservice.controller;

import com.workhive.projectservice.domain.dto.TaskDto;
import com.workhive.projectservice.domain.dto.TaskRequest;
import com.workhive.projectservice.domain.entity.Task.TaskStatus;
import com.workhive.projectservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskRequest request) {
        log.info("REST request to create task: {}", request.getTitle());
        TaskDto createdTask = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        log.info("REST request to get task: {}", id);
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        log.info("REST request to get all tasks");
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDto>> getTasksByProject(@PathVariable Long projectId) {
        log.info("REST request to get tasks by project: {}", projectId);
        List<TaskDto> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable TaskStatus status) {
        log.info("REST request to get tasks by status: {}", status);
        List<TaskDto> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/developer/{developerId}")
    public ResponseEntity<List<TaskDto>> getTasksByDeveloper(@PathVariable Long developerId) {
        log.info("REST request to get tasks by developer: {}", developerId);
        List<TaskDto> tasks = taskService.getTasksByDeveloper(developerId);
        return ResponseEntity.ok(tasks);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        log.info("REST request to update task: {}", id);
        TaskDto updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(updatedTask);
    }
    
    @PutMapping("/{id}/assign")
    public ResponseEntity<TaskDto> assignDevelopers(
            @PathVariable Long id,
            @RequestBody List<Long> developerIds) {
        log.info("REST request to assign developers to task: {}", id);
        TaskDto updatedTask = taskService.assignDevelopers(id, developerIds);
        return ResponseEntity.ok(updatedTask);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        log.info("REST request to update task status: {} to {}", id, status);
        TaskDto updatedTask = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("REST request to delete task: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
