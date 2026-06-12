package lightprojects.pz2.service;

import lightprojects.pz2.dto.AuthDtos.AuthResponse;
import lightprojects.pz2.dto.AuthDtos.LoginRequest;
import lightprojects.pz2.dto.AuthDtos.RegisterRequest;
import lightprojects.pz2.dto.AuthDtos.UserResponse;
import lightprojects.pz2.entity.Role;
import lightprojects.pz2.entity.User;
import lightprojects.pz2.repository.RoleRepository;
import lightprojects.pz2.repository.UserRepository;
import lightprojects.pz2.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthService {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        Role userRole = roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not initialized"));

        User user = new User(request.username(), request.email(),
                passwordEncoder.encode(request.password()));
        user.addRole(userRole);

        return toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.usernameOrEmail(), request.password()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = userRepository.findByUsernameOrEmail(
                        request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String roles = String.join(",", roleNames(user));
        String token = jwtService.generateToken(user.getUsername(), roles);

        return new AuthResponse(token, toUserResponse(user));
    }

    public static UserResponse toUserResponse(User user) {
        List<String> roles = roleNames(user);

        String primary = roles.contains("ADMIN") ? "ADMIN" : "USER";
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), primary, roles);
    }

    private static List<String> roleNames(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .map(name -> name.startsWith("ROLE_") ? name.substring(5) : name)
                .toList();
    }
}
