package mtel.repository;

import jakarta.validation.constraints.NotBlank;
import mtel.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsernameAndPassword(@NotBlank String username, @NotBlank String password);

    boolean existsByUsername(@NotBlank String username);
}
