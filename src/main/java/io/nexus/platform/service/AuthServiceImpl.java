package io.nexus.platform.service;


import io.nexus.platform.dto.request.LoginRequest;
import io.nexus.platform.dto.request.RegisterRequest;
import io.nexus.platform.dto.response.AuthResponse;
import io.nexus.platform.entity.Tenant;
import io.nexus.platform.entity.User;
import io.nexus.platform.enums.Role;
import io.nexus.platform.exception.BadRequestException;
import io.nexus.platform.exception.ResourceNotFoundException;
import io.nexus.platform.exception.UnauthorizedException;
import io.nexus.platform.repository.TenantRepository;
import io.nexus.platform.repository.UserRepository;
import io.nexus.platform.security.JwtService;
import io.nexus.platform.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (tenantRepository.existsBySlug(request.getTenantSlug())) {
            throw new BadRequestException("Tenant slug already exists");
        }

        Tenant tenant = Tenant.builder()
                .name(request.getTenantName())
                .slug(request.getTenantSlug())
                .isActive(true)
                .build();
        tenantRepository.save(tenant);

        if (userRepository.existsByEmailAndTenantId(request.getEmail(), tenant.getId())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .tenant(tenant)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.ADMIN)
                .isActive(true)
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                tenant.getSlug(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .tenantSlug(tenant.getSlug())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Tenant tenant = tenantRepository.findBySlug(request.getTenantSlug())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User user = userRepository.findByEmailAndTenantId(request.getEmail(), tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                tenant.getSlug(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .tenantSlug(tenant.getSlug())
                .build();
    }
}