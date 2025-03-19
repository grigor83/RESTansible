package mtel.services;

import jakarta.transaction.Transactional;
import mtel.dto.HostDTO;
import mtel.model.Inventory;
import mtel.model.Playbook;
import mtel.repository.InventoryRepository;
import mtel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    public List<Inventory> loadInventories(int userId) {
        return inventoryRepository.findByUserIdOrUserIdIsNull(userId);
    }

    public UrlResource loadFile(String filename) throws MalformedURLException {
        Inventory inventory = inventoryRepository.findByFilename(filename);
        Path filePath = Paths.get(inventory.getFilepath());
        UrlResource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read file: " + filename);
        }
    }


    public String saveFile(MultipartFile file, Integer userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Fajl ne sme biti prazan!");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        // Kreiraj upload folder ako ne postoji
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        // Sačuvaj playbook u bazu
        Inventory inventory = new Inventory();
        inventory.setFilename(file.getOriginalFilename());
        inventory.setFilepath(filePath.toString());
        inventory.setUser(userRepository.findById(userId).get());
        inventory = inventoryRepository.save(inventory);
        System.out.println(inventory);

        return "Inventory uploaded successfully: " + fileName;
    }

    public String updateFile(MultipartFile file, int inventoryId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Fajl ne sme biti prazan!");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory not found!");
        }
        Files.write(Path.of(inventory.getFilepath()), file.getBytes());

        return "Inventory updated successfully: " + inventory.getFilename();
    }

    public List<HostDTO> loadHostsNames(int userId) throws IOException {
        // loads hosts name or group name from inventory file
        List<String> names = new ArrayList<>();

        // Učitava sadržaj inventory fajla iz resources/ansible koristeći InputStream
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("ansible/hosts").getInputStream()))) {
            names.addAll(reader.lines()
                    .filter(line -> line.contains("ansible_host") || line.contains("["))
                    .toList());
        }
        // Učitava custom inventory fajl iz baze ako postoji
        inventoryRepository.findByUserId(userId).ifPresent(inventory -> {
            try {
                Path filePath = Path.of(inventory.getFilepath());
                names.addAll(Files.lines(filePath)
                        .filter(line -> line.contains("ansible_host") || line.contains("["))
                        .toList());
            } catch (IOException e) {
                throw new RuntimeException("Error reading inventory file: " + inventory.getFilepath(), e);
            }
        });

        List<HostDTO> hosts = parseNames(names);
        loadPlaybooksForHosts(userId, hosts);
        //playbookService.loadPlaybooksForHosts(userId, hosts);

        return hosts;
    }

    private void loadPlaybooksForHosts(int userId, List<HostDTO> hosts) throws IOException {
        Map<String, List<String>> fileNames = new LinkedHashMap<>();

        //Ucitava sve playbooks i provjerava da li sadrze ima hosta u svom sadrzaju
        List<Playbook> userPlaybooks = playbookService.getPlaybooks(userId);
        if (!userPlaybooks.isEmpty()) {
            for (Playbook playbook : userPlaybooks) {
                try {
                    String content = null;
                    List<String> lines = null;
                    if (playbook.getFilepath() == null){
                        content = playbookService.readFileFromResources(playbook.getFilename());
                        lines = List.of(content.split("\n"));
                    }
                    else {
                        Path filePath = Path.of(playbook.getFilepath());
                        lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                    }

                    for (String line : lines) {
                        if (line.contains("hosts:")) {
                            fileNames.put(playbook.getFilename(), Arrays.asList(line.split(":")[1].trim().split(",")));
                        }
                    }
                } catch (IOException i){
                    continue;
                }
            }
        }

        fileNames.forEach((key, value) -> {
            for (HostDTO host : hosts) {
                if (value.contains(host.getGroupName()) || value.contains(host.getName().split(" ")[0].trim()) || value.contains("all")) {
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
                hosts.add(new HostDTO(section, line));
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
