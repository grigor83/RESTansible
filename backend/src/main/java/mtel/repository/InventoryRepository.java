package mtel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mtel.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    List<Inventory> findByUserIdOrUserIdIsNull(int userId);

    Inventory findByFilename(String filename);

    Optional<Inventory> findByUserId(int userId);
}
