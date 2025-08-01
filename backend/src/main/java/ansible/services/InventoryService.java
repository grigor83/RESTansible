package ansible.services;

import jakarta.transaction.Transactional;
import ansible.dto.HostDTO;
import ansible.model.Inventory;
import ansible.model.Playbook;
import ansible.repository.InventoryRepository;
import ansible.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ansible.RestAnsibleApplication.readFileFromResources;

@Service
@Transactional
public class InventoryService {
    private final PlaybookService playbookService;
    @Value("${upload.path}")
    private String uploadDir;

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, UserRepository userRepository, PlaybookService playbookService) {
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.playbookService = playbookService;
    }

    public List<Inventory> getInventories(int userId) {
        return inventoryRepository.findByUserIdOrUserIdIsNull(userId);
    }

    public String loadInventoryContent(Integer inventoryId) throws IOException {
        Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
        if (inventory.isEmpty()) {
            throw new IOException("Playbook not found");
        }

        if (inventory.get().getFilepath() == null)
            return readFileFromResources(inventory.get().getFilename());

        return Files.readString(Path.of(inventory.get().getFilepath()));
    }

    public String updateFile(Integer inventoryId, String content) throws IOException {
        Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
        if (inventory.isEmpty()) {
            throw new IOException("Inventory not found");
        }

        if (inventory.get().getFilepath() == null)
            throw new IOException("Cannot update inventory in resources!");

        Files.writeString(Path.of(inventory.get().getFilepath()), content);
        return "Playbook updated successfully.";
    }

    public Inventory createFile(Integer userId, String filename, String content) throws IOException {
        if (userId == null || filename == null || filename.isEmpty() ||
                content == null || content.isEmpty()) {
            throw new IOException("User ID, filename or content cannot be empty");
        }

        Path filePath = Paths.get(uploadDir, UUID.randomUUID() + "_" + filename);
        // Kreiraj upload folder ako ne postoji
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);

        Inventory inventory = new Inventory();
        inventory.setUser(userRepository.findById(userId).get());
        inventory.setFilename(filename);
        inventory.setFilepath(filePath.toFile().getAbsolutePath());

        return inventoryRepository.save(inventory);
    }

    public String deleteInventory(Integer id) throws IllegalArgumentException, IOException {
        if (inventoryRepository.existsById(id)) {
            Optional<Inventory> inventory = inventoryRepository.findById(id);
            if (inventory.get().getFilepath() == null)
                throw new IllegalArgumentException("Playbook from recources cannot be deleted!");
            inventoryRepository.deleteById(id);
            Files.delete(Path.of(inventory.get().getFilepath()));
            return "Playbook deleted successfully.";
        }

        throw new IllegalArgumentException("Playbook id is invalid!");
    }

    public Map<String, List<?>> getFileNames(int userId) throws IOException {
        // loads hosts name or group name from inventory file
        List<String> names = new ArrayList<>();

        List<Inventory> inventories = inventoryRepository.findByUserIdOrUserIdIsNull(userId);
        if (!inventories.isEmpty()) {
            for (Inventory inventory : inventories) {
                try {
                    String content = null;
                    List<String> lines = null;
                    if (inventory.getFilepath() == null){
                        content = readFileFromResources(inventory.getFilename());
                        lines = List.of(content.split("\n"));
                    }
                    else {
                        Path filePath = Path.of(inventory.getFilepath());
                        lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                    }

                    for (String line : lines) {
                        if (line.contains("ansible_host") || line.contains("[")) {
                            names.add(line);
                        }
                    }
                } catch (IOException i){
                    continue;
                }
            }
        }
        
        List<HostDTO> hosts = parseNames(names);
        loadPlaybooksForHosts(userId, hosts);

        Map<String, List<?>> response = new HashMap<>();
        response.put("hosts", hosts);
        response.put("inventories", inventories);
        return response;
    }

    private void loadPlaybooksForHosts(int userId, List<HostDTO> hosts) throws IOException {
        Map<String, Set<String>> fileNames = new LinkedHashMap<>();

        //Ucitava sve playbooks i provjerava da li sadrze ime hosta u svom sadrzaju
        // Ako playbook sadrzi ime hosta, ime playbook fajla se dodaje u listu playbooka hosta
        List<Playbook> userPlaybooks = playbookService.getPlaybooks(userId);
        if (!userPlaybooks.isEmpty()) {
            for (Playbook playbook : userPlaybooks) {
                try {
                    String content = null;
                    List<String> lines = null;
                    if (playbook.getFilepath() == null){
                        content = readFileFromResources(playbook.getFilename());
                        lines = List.of(content.split("\n"));
                    }
                    else {
                        Path filePath = Path.of(playbook.getFilepath());
                        lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                    }

                    for (String line : lines) {
                        if (line.contains("hosts:")) {
                            //fileNames.put(playbook.getFilename(), Arrays.asList(line.split(":")[1].trim().split(",")));
                            Set<String> newHosts = Arrays.stream(line.split(":")[1].trim().split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toSet());
                            // Dodaj u mapu — automatski spaja nove hostove s postojećima
                            fileNames.merge(
                                    playbook.getFilename(),
                                    newHosts,
                                    (existing, incoming) -> {
                                        existing.addAll(incoming);
                                        return existing;
                                    }
                            );
                        }
                    }
                } catch (IOException i){
                    continue;
                }
            }
        }

        fileNames.forEach((key, value) -> {
            for (HostDTO host : hosts) {
                if (value.contains(host.getGroupName().trim().replaceAll("[\\[\\]]", "")) ||
                        value.contains(host.getName().split(" ")[0].trim()) ||
                        value.contains("all")) {
                    Optional<Playbook> playbook = userPlaybooks.stream()
                            .filter(p -> p.getFilename().equals(key))
                            .findAny();
                    if (playbook.isPresent()) {
                        host.getPlaybooks().add(playbook.get());
                    }
                    else {
                        Playbook newPlaybook = new Playbook();
                        newPlaybook.setFilename(key);
                        host.getPlaybooks().add(newPlaybook);
                    }
                }
            }
        });
    }

    private List<HostDTO> parseNames(List<String> names) {
        Map<String, List<String>> sections = new LinkedHashMap<>();
        String currentSection = null;
        List<String> standaloneHosts = new ArrayList<>();

        for (String line : names) {
            if (line.isEmpty())
                continue;

            if (line.contains("[")) {  // Match any section header like [iosxr]
                currentSection = line.contains(":vars") ? null : line;
                continue; // Move to the next line
            }

            if (line.contains("ansible_host")) {
                if (currentSection != null) {
                    sections.computeIfAbsent(currentSection, k -> new ArrayList<>()).add(line);
                } else {
                    standaloneHosts.add(line);
                }
            }
        }

        List<HostDTO> hosts = new ArrayList<>();
        sections.forEach((section, lines) -> {
            lines.forEach(line -> {
                hosts.add(new HostDTO(section.trim(), line.trim()));
            });
        });

        if (!standaloneHosts.isEmpty()) {
            standaloneHosts.forEach(line -> {
                hosts.add(new HostDTO(line));
            });
        }

        return hosts;
    }


}
