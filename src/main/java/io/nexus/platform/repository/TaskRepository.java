package io.nexus.platform.repository;

import io.nexus.platform.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByTenantId(UUID tenantId);
    List<Task> findAllByProjectId(UUID projectId);
    List<Task> findAllByAssignedToId(UUID userId);
}