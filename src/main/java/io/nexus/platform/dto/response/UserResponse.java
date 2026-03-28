package io.nexus.platform.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String tenantSlug;
}
