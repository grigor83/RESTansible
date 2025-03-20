package ansible.services;

import ansible.RestAnsibleApplication;
import jakarta.transaction.Transactional;
import ansible.model.Playbook;
import ansible.repository.PlaybookRepository;
import ansible.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Transactional
public class PlaybookService {

    @Value("${upload.path}")
    private String uploadDir;

    private final PlaybookRepository playbookRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlaybookService(PlaybookRepository playbookRepository, UserRepository userRepository) {
        this.playbookRepository = playbookRepository;
        this.userRepository = userRepository;
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

        Path filePath = Paths.get(uploadDir, UUID.randomUUID() + "_" + filename);
        // Kreiraj upload folder ako ne postoji
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);

        Playbook playbook = new Playbook();
        playbook.setUser(userRepository.findById(userId).get());
        playbook.setFilename(filename);
        playbook.setFilepath(filePath.toString());

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

}
