package io.nexus.platform.service;

import io.nexus.platform.context.TenantContext;
import io.nexus.platform.dto.request.CreateTaskRequest;
import io.nexus.platform.dto.response.TaskResponse;
import io.nexus.platform.entity.Project;
import io.nexus.platform.entity.Task;
import io.nexus.platform.entity.Tenant;
import io.nexus.platform.entity.User;
import io.nexus.platform.enums.TaskStatus;
import io.nexus.platform.exception.BadRequestException;
import io.nexus.platform.exception.ResourceNotFoundException;
import io.nexus.platform.repository.ProjectRepository;
import io.nexus.platform.repository.TaskRepository;
import io.nexus.platform.repository.TenantRepository;
import io.nexus.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    private Tenant getCurrentTenant() {
        String slug = TenantContext.getTenantSlug();
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Tenant tenant = getCurrentTenant();
        return userRepository.findByEmailAndTenantId(email, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Tenant tenant = getCurrentTenant();
        User creator = getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getTenant().getId().equals(tenant.getId())) {
            throw new BadRequestException("Project does not belong to your tenant");
        }

        User assignedTo = null;
        if (request.getAssignedTo() != null) {
            assignedTo = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
        }

        Task task = Task.builder()
                .tenant(tenant)
                .project(project)
                .createdBy(creator)
                .assignedTo(assignedTo)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TaskStatus.TODO)
                .dueDate(request.getDueDate())
                .build();

        taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        Tenant tenant = getCurrentTenant();
        return taskRepository.findAllByTenantId(tenant.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(UUID id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        try {
            task.setStatus(TaskStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
        taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .assignedTo(task.getAssignedTo() != null ?
                        task.getAssignedTo().getEmail() : null)
                .createdBy(task.getCreatedBy().getEmail())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
