package lightprojects.pz2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 3, max = 100) String password) {
    }

    public record LoginRequest(
            @NotBlank String usernameOrEmail,
            @NotBlank String password) {
    }

    public record UserResponse(
            Long id,
            String username,
            String email,
            String role,
            List<String> roles) {
    }

    public record AuthResponse(
            String token,
            UserResponse user) {
    }
}
