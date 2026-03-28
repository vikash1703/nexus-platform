package io.nexus.platform.security;

import io.nexus.platform.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private UUID id;
    private String email;
    private String tenantSlug;
    private String role;

    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getTenant().getSlug(),
                user.getRole().name()
        );
    }
}
