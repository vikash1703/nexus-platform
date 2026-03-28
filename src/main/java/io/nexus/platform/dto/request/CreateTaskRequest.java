package io.nexus.platform.dto.request;

import io.nexus.platform.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Project is required")
    private UUID projectId;

    private UUID assignedTo;

    private Priority priority = Priority.MEDIUM;

    private LocalDateTime dueDate;
}
