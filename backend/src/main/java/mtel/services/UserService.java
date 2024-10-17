package mtel.services;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mtel.PaswordHasher;
import mtel.UsernameAlreadyExistsException;
import mtel.models.User;
import mtel.repository.UserRepository;
import org.springframework.stereotype.Service;
import mtel.BadCredentialsException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PaswordHasher hasher;

    public UserService(UserRepository userRepository, PaswordHasher hasher) {
        this.userRepository = userRepository;
        this.hasher = hasher;
    }

    public User loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameAndPassword(
                            loginRequest.username(), hasher.hashPassword(loginRequest.password))
                .orElseThrow(BadCredentialsException::new);

        return user;
    }

    public void registerUser(User user) {
        if (this.userRepository.existsByUsername(user.getUsername()))
            throw new UsernameAlreadyExistsException("Username: " + user.getUsername() + " is already taken!");

        user.setPassword(hasher.hashPassword(user.getPassword()));
        userRepository.save(user);
    }


    public record LoginRequest(
            @NotBlank
            String username,
            @NotBlank
            String password)
    {}
}
