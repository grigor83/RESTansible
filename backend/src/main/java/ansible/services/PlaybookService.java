package ansible.services;

import ansible.RestAnsibleApplication;
import ansible.model.Inventory;
import ansible.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import ansible.model.Playbook;
import ansible.repository.PlaybookRepository;
import ansible.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Transactional
public class PlaybookService {

    private final InventoryRepository inventoryRepository;
    @Value("${upload.path}")
    private String uploadsDir;

    private final PlaybookRepository playbookRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlaybookService(PlaybookRepository playbookRepository, UserRepository userRepository, InventoryRepository inventoryRepository) {
        this.playbookRepository = playbookRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Playbook> getPlaybooks(int userId) throws IOException {
        //Load all playbooks from database, users and playbooks from resource folder
        return new ArrayList<>(playbookRepository.findAllPlaybooks(userId));
    }

    public String loadPlaybookContent(Integer playbookId) throws IOException {
        Optional<Playbook> playbook = playbookRepository.findById(playbookId);
        if (playbook.isEmpty()) {
            throw new IOException("Playbook not found");
        }

        if (playbook.get().getFilepath() == null)
            return RestAnsibleApplication.readFileFromResources(playbook.get().getFilename());

        return Files.readString(Path.of(playbook.get().getFilepath()));
    }

    public String updateFile(Integer playbookId, String content) throws IOException {
        Optional<Playbook> playbook = playbookRepository.findById(playbookId);
        if (playbook.isEmpty()) {
            throw new IOException("Playbook not found");
        }

        if (playbook.get().getFilepath() == null)
            throw new IOException("Cannot update playbook in resources!");

        Files.writeString(Path.of(playbook.get().getFilepath()), content);
        return "Playbook updated successfully.";
    }

    public Playbook createFile(Integer userId, String filename, String content) throws IOException {
        if (userId == null || filename == null || filename.isEmpty() ||
                content == null || content.isEmpty()) {
            throw new IOException("User ID, filename or content cannot be empty");
        }

        Path filePath = Paths.get(uploadsDir, UUID.randomUUID() + "_" + filename);
        // Kreiraj upload folder ako ne postoji
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);

        Playbook playbook = new Playbook();
        playbook.setUser(userRepository.findById(userId).get());
        playbook.setFilename(filename);
        playbook.setFilepath(filePath.toFile().getAbsolutePath());
        
        return playbookRepository.save(playbook);
    }

    public String deletePlaybook(Integer id) throws IllegalArgumentException, IOException {
        if (playbookRepository.existsById(id)) {
            Optional<Playbook> playbook = playbookRepository.findById(id);
            if (playbook.get().getFilepath() == null)
                throw new IllegalArgumentException("Playbook from recources cannot be deleted!");
            playbookRepository.deleteById(id);
            Files.delete(Path.of(playbook.get().getFilepath()));
            return "Playbook deleted successfully.";
        }

        throw new IllegalArgumentException("Playbook id is invalid!");
    }

    public Map<String, String> runPlaybook(Integer playbookId, Integer inventoryId) throws Exception {
        Map<String, String> response = new HashMap<>();

        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
        Playbook playbook = playbookRepository.findById(playbookId).orElse(null);
        if (playbook == null) {
            throw new Exception("Playbook not found");
        }
        if (inventory == null) {
            throw new Exception("Inventory not found");
        }

        String playbookPath = "", inventoryPath = "";
        if (playbook.getFilepath() == null) {
            // Extract playbook file from resources
            playbookPath = RestAnsibleApplication.copyResourceFile(playbook.getFilename());
        }
        else
            playbookPath = playbook.getFilepath();
        if (inventory.getFilepath() == null) {
            // Extract inventory file from resources
            inventoryPath = RestAnsibleApplication.copyResourceFile(inventory.getFilename());
        }
        else
            inventoryPath = inventory.getFilepath();
        // Convert to WSL format
        String wslPlaybookPath = RestAnsibleApplication.convertToWslPath(playbookPath);
        String wslInventoryPath = RestAnsibleApplication.convertToWslPath(inventoryPath);
        // Run Ansible in WSL
        ProcessBuilder processBuilder = new ProcessBuilder("wsl", "ansible-playbook", "-i", wslInventoryPath, wslPlaybookPath);
        //processBuilder.directory(workingDir);
        processBuilder.redirectErrorStream(true);
        // Start the process
        Process process = processBuilder.start();

        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder outputBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            outputBuffer.append(line).append("\n");
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();
        response.put("output", outputBuffer.toString());
        response.put("exitCode", String.valueOf(exitCode));
        return response;
    }
}
