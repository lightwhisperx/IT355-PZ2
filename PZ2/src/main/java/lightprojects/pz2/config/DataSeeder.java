package lightprojects.pz2.config;

import lightprojects.pz2.entity.Category;
import lightprojects.pz2.entity.Role;
import lightprojects.pz2.entity.User;
import lightprojects.pz2.repository.CategoryRepository;
import lightprojects.pz2.repository.RoleRepository;
import lightprojects.pz2.repository.UserRepository;
import lightprojects.pz2.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role userRole = ensureRole(AuthService.ROLE_USER);
        Role adminRole = ensureRole(AuthService.ROLE_ADMIN);

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@admin.com", passwordEncoder.encode("admin"));
            admin.addRole(adminRole);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user")) {
            User user = new User("user", "user@test.com", passwordEncoder.encode("123"));
            user.addRole(userRole);
            userRepository.save(user);
        }

        List<String> defaults = List.of(
                "Technology", "Science", "Travel", "Food", "Sports", "Entertainment", "Misc");
        for (String name : defaults) {
            if (!categoryRepository.existsByNameIgnoreCase(name)) {
                categoryRepository.save(new Category(name));
            }
        }
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(new Role(name)));
    }
}
