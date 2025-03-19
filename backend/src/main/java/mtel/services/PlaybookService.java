package mtel.services;

import jakarta.transaction.Transactional;
import mtel.dto.HostDTO;
import mtel.model.Playbook;
import mtel.repository.PlaybookRepository;
import mtel.repository.UserRepository;
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
//        List<Playbook> playbooks;
//        Path folderPath = new ClassPathResource("ansible").getFile().toPath();
//        //Get names of playbooks in folder resource/ansible
//        try (var stream = Files.list(folderPath)) {
//            playbooks = stream.filter(Files::isRegularFile)
//                    .map(Path::getFileName)
//                    .map(Path::toString)
//                    .filter(name -> name.endsWith(".yaml") || name.endsWith(".yml"))
//                    .map(Playbook::new)
//                    .toList();
//        }

        //Load all playbooks from database, users and playbooks from resource folder
        List<Playbook> allPlaybooks = new ArrayList<>(playbookRepository.findAllPlaybooks(userId));
        return allPlaybooks;
    }

    public String loadPlaybookContent(Integer playbookId) throws IOException {
        Optional<Playbook> playbook = playbookRepository.findById(playbookId);
        if (playbook.isEmpty()) {
            throw new IOException("Playbook not found");
        }

        if (playbook.get().getFilepath() == null)
            return readFileFromResources(playbook.get().getFilename());

        //Path filePath = Paths.get(uploadDir, filename);
        return Files.readString(Path.of(playbook.get().getFilepath()));
    }

    public String updateFile(Integer playbookId, String content) throws IOException {
        Optional<Playbook> playbook = playbookRepository.findById(playbookId);
        if (playbook.isEmpty()) {
            throw new IllegalArgumentException("Playbook not found");
        }

        if (playbook.get().getFilepath() == null)
            throw new IOException("Cannot update playbook in resources!");

        Files.writeString(Path.of(playbook.get().getFilepath()), content);
        return "Playbook updated successfully.";
    }


    public Playbook createFile(Integer userId, String filename, String content) throws IOException {
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


    public void loadPlaybooksForHosts(int userId, List<HostDTO> hosts) throws IOException {
        Map<String, List<String>> fileNames = new LinkedHashMap<>();
//
//        // ucitava imena playbook fajlova iz foldera resource/ansible, ako sadrze ime hosta u svom sadrzaju
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        Resource[] resources = resolver.getResources("classpath*:ansible/*.yaml"); // Load all .yml files from resource/ansible
//        for (Resource resource : resources) {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    if (line.contains("hosts:")) {
//                        fileNames.put(resource.getFilename(), Arrays.asList(line.split(":")[1].trim().split(",")));
//                    }
//                }
//            }
//        }

        //Ucitava sve playbooks i provjerava da li sadrze ima hosta u svom sadrzaju
        List<Playbook> userPlaybooks = playbookRepository.findAllPlaybooks(userId);
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

        //fileNames.forEach((key, value) -> {System.out.println(key + ": " + value);});
    }

    public String readFileFromResources(String filename) throws IOException {
        // Prednosti korišćenja ClassPathResource: Radi i kada se aplikacija build-a u .jar ili .war.
        // Ne zavisi od putanja na operativnom sistemu.
        Resource resource = new ClassPathResource("ansible/" + filename);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

}
