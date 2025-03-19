package mtel.repository;

import jakarta.validation.constraints.NotBlank;
import mtel.model.Playbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PlaybookRepository extends JpaRepository<Playbook, Integer> {

    @Query(value = "SELECT * FROM playbook WHERE user_id=:userId OR user_id IS NULL", nativeQuery = true)
    List<Playbook> findAllPlaybooks(Integer userId);

    Playbook findByFilename(@NotBlank String filename);
}
