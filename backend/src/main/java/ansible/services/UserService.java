package ansible.services;

import ansible.PasswordHasher;
import jakarta.transaction.Transactional;
import ansible.model.User;
import ansible.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }


    public User loadUser(User user) {
        return userRepository.findByUsernameAndPassword(user.getUsername(), passwordHasher.hashPassword(user.getPassword()))
                .orElse(null);
    }

    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            return null;

        user.setPassword(passwordHasher.hashPassword(user.getPassword()));
        return userRepository.save(user);
    }
}
