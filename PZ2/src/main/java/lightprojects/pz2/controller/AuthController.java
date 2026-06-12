package lightprojects.pz2.controller;

import jakarta.validation.Valid;
import lightprojects.pz2.dto.AuthDtos.AuthResponse;
import lightprojects.pz2.dto.AuthDtos.LoginRequest;
import lightprojects.pz2.dto.AuthDtos.RegisterRequest;
import lightprojects.pz2.dto.AuthDtos.UserResponse;
import lightprojects.pz2.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
