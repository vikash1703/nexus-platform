package io.nexus.platform.service;

import io.nexus.platform.dto.request.CreateTaskRequest;
import io.nexus.platform.dto.response.TaskResponse;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request);
    List<TaskResponse> getAllTasks();
    TaskResponse getTaskById(UUID id);
    TaskResponse updateTaskStatus(UUID id, String status);
    void deleteTask(UUID id);
}
