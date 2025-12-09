package com.workhive.projectservice.service.impl;

import com.workhive.projectservice.client.UserServiceClient;
import com.workhive.projectservice.client.dto.UserDto;
import com.workhive.projectservice.domain.dto.TaskDto;
import com.workhive.projectservice.domain.dto.TaskRequest;
import com.workhive.projectservice.domain.entity.Project;
import com.workhive.projectservice.domain.entity.Task;
import com.workhive.projectservice.domain.entity.Task.TaskStatus;
import com.workhive.projectservice.exception.ResourceNotFoundException;
import com.workhive.projectservice.mapper.TaskMapper;
import com.workhive.projectservice.repository.ProjectRepository;
import com.workhive.projectservice.repository.TaskRepository;
import com.workhive.projectservice.service.TaskService;
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
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final UserServiceClient userServiceClient;
    
    @Override
    @Transactional
    @CircuitBreaker(name = "taskService", fallbackMethod = "createTaskFallback")
    @Retry(name = "taskService")
    public TaskDto createTask(TaskRequest request) {
        log.info("Creating task: {}", request.getTitle());
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
        
        Task task = taskMapper.toEntity(request, project);
        Task savedTask = taskRepository.save(task);
        return enrichTaskDto(taskMapper.toDto(savedTask));
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "taskService", fallbackMethod = "getTaskFallback")
    @Retry(name = "taskService")
    public TaskDto getTaskById(Long id) {
        log.info("Fetching task by id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return enrichTaskDto(taskMapper.toDto(task));
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "taskService", fallbackMethod = "getAllTasksFallback")
    public List<TaskDto> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .map(this::enrichTaskDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "taskService", fallbackMethod = "getTasksByProjectFallback")
    public List<TaskDto> getTasksByProject(Long projectId) {
        log.info("Fetching tasks by project: {}", projectId);
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toDto)
                .map(this::enrichTaskDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "taskService", fallbackMethod = "getTasksByStatusFallback")
    public List<TaskDto> getTasksByStatus(TaskStatus status) {
        log.info("Fetching tasks by status: {}", status);
        return taskRepository.findByStatus(status).stream()
                .map(taskMapper::toDto)
                .map(this::enrichTaskDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "taskService", fallbackMethod = "getTasksByDeveloperFallback")
    public List<TaskDto> getTasksByDeveloper(Long developerId) {
        log.info("Fetching tasks by developer: {}", developerId);
        return taskRepository.findByDeveloperId(developerId).stream()
                .map(taskMapper::toDto)
                .map(this::enrichTaskDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    @CircuitBreaker(name = "taskService", fallbackMethod = "updateTaskFallback")
    @Retry(name = "taskService")
    public TaskDto updateTask(Long id, TaskRequest request) {
        log.info("Updating task: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskMapper.updateEntity(task, request);
        Task updatedTask = taskRepository.save(task);
        return enrichTaskDto(taskMapper.toDto(updatedTask));
    }
    
    @Override
    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task: {}", id);
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    @CircuitBreaker(name = "taskService", fallbackMethod = "assignDevelopersFallback")
    @Retry(name = "taskService")
    public TaskDto assignDevelopers(Long taskId, List<Long> developerIds) {
        log.info("Assigning developers to task: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setDeveloperIds(developerIds);
        Task updatedTask = taskRepository.save(task);
        return enrichTaskDto(taskMapper.toDto(updatedTask));
    }
    
    @Override
    @Transactional
    @CircuitBreaker(name = "taskService", fallbackMethod = "updateTaskStatusFallback")
    @Retry(name = "taskService")
    public TaskDto updateTaskStatus(Long taskId, TaskStatus status) {
        log.info("Updating task status: {} to {}", taskId, status);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return enrichTaskDto(taskMapper.toDto(updatedTask));
    }
    
    private TaskDto enrichTaskDto(TaskDto taskDto) {
        try {
            // Fetch developer information if developer IDs are present
            if (taskDto.getDeveloperIds() != null && !taskDto.getDeveloperIds().isEmpty()) {
                List<UserDto> developers = userServiceClient.getUsersByIds(taskDto.getDeveloperIds());
                List<String> developerNames = developers.stream()
                        .map(UserDto::getFullName)
                        .collect(Collectors.toList());
                taskDto.setDeveloperNames(developerNames);
            }
        } catch (Exception e) {
            log.error("Error enriching task DTO: {}", e.getMessage());
        }
        return taskDto;
    }
    
    // Fallback methods
    private TaskDto createTaskFallback(TaskRequest request, Exception e) {
        log.error("Fallback: createTask failed", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
    
    private TaskDto getTaskFallback(Long id, Exception e) {
        log.error("Fallback: getTaskById failed for id: {}", id, e);
        throw new ResourceNotFoundException("Task service unavailable");
    }
    
    private List<TaskDto> getAllTasksFallback(Exception e) {
        log.error("Fallback: getAllTasks failed", e);
        return List.of();
    }
    
    private List<TaskDto> getTasksByProjectFallback(Long projectId, Exception e) {
        log.error("Fallback: getTasksByProject failed for project: {}", projectId, e);
        return List.of();
    }
    
    private List<TaskDto> getTasksByStatusFallback(TaskStatus status, Exception e) {
        log.error("Fallback: getTasksByStatus failed for status: {}", status, e);
        return List.of();
    }
    
    private List<TaskDto> getTasksByDeveloperFallback(Long developerId, Exception e) {
        log.error("Fallback: getTasksByDeveloper failed for developer: {}", developerId, e);
        return List.of();
    }
    
    private TaskDto updateTaskFallback(Long id, TaskRequest request, Exception e) {
        log.error("Fallback: updateTask failed for id: {}", id, e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
    
    private TaskDto assignDevelopersFallback(Long taskId, List<Long> developerIds, Exception e) {
        log.error("Fallback: assignDevelopers failed for task: {}", taskId, e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
    
    private TaskDto updateTaskStatusFallback(Long taskId, TaskStatus status, Exception e) {
        log.error("Fallback: updateTaskStatus failed for task: {}", taskId, e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
}
