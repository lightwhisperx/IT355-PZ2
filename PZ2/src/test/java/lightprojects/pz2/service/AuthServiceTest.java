package lightprojects.pz2.service;

import lightprojects.pz2.dto.AuthDtos.RegisterRequest;
import lightprojects.pz2.dto.AuthDtos.UserResponse;
import lightprojects.pz2.entity.Role;
import lightprojects.pz2.entity.User;
import lightprojects.pz2.repository.RoleRepository;
import lightprojects.pz2.repository.UserRepository;
import lightprojects.pz2.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_hashesPasswordAndAssignsUserRole() {
        when(userRepository.existsByUsername("newbie")).thenReturn(false);
        when(userRepository.existsByEmail("newbie@test.com")).thenReturn(false);
        when(roleRepository.findByName(AuthService.ROLE_USER))
                .thenReturn(Optional.of(new Role(AuthService.ROLE_USER)));
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(42L);
            return u;
        });

        UserResponse response = authService.register(
                new RegisterRequest("newbie", "newbie@test.com", "secret"));

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.username()).isEqualTo("newbie");
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.roles()).containsExactly("USER");

        verify(passwordEncoder).encode("secret");
    }

    @Test
    void register_withExistingUsername_isConflict() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("admin", "a@test.com", "secret")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository, never()).save(any());
    }
}
