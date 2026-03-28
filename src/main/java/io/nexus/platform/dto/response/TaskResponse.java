package io.nexus.platform.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TaskResponse {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assignedTo;
    private String createdBy;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}