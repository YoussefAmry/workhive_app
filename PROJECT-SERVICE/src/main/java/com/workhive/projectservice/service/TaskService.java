package com.workhive.projectservice.service;

import com.workhive.projectservice.domain.dto.TaskDto;
import com.workhive.projectservice.domain.dto.TaskRequest;
import com.workhive.projectservice.domain.entity.Task.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskDto createTask(TaskRequest request);
    TaskDto getTaskById(Long id);
    List<TaskDto> getAllTasks();
    List<TaskDto> getTasksByProject(Long projectId);
    List<TaskDto> getTasksByStatus(TaskStatus status);
    List<TaskDto> getTasksByDeveloper(Long developerId);
    TaskDto updateTask(Long id, TaskRequest request);
    void deleteTask(Long id);
    TaskDto assignDevelopers(Long taskId, List<Long> developerIds);
    TaskDto updateTaskStatus(Long taskId, TaskStatus status);
}
