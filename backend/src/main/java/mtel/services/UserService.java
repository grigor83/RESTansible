package mtel.services;

import jakarta.transaction.Transactional;
import mtel.model.User;
import mtel.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User loadUser(User user) {
        return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword()).orElse(null);
    }

    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            return null;

        return userRepository.save(user);
    }
}
