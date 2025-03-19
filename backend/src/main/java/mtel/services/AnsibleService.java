package mtel.services;

import mtel.model.Playbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnsibleService {
    // Specify the directory where you want to execute the command
    public static final File ANSIBLE_DIR = new File(System.getProperty("user.dir")
            + File.separator + "src\\main\\resources\\ansible");
    public static final List<String> PLAYBOOKS_NAMES = List.of("create_loopback0.yaml", "create_loopbacks.yaml",
            "delete_loopback0.yaml", "interfaces_facts.yaml", "ios_facts.yaml", "list_interfaces.yaml");

    private final DataService dataService;

    public AnsibleService(DataService dataService) {
        this.dataService = dataService;
    }

    public Map<String, String> runPlaybook(Playbook playbook) throws Exception {
        Map<String, String> response = new HashMap<>();

        //String bashCommand = choosePlaybook(playbookName);
        // Create a process builder
        //ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", bashCommand);
        ProcessBuilder processBuilder = new ProcessBuilder("wsl", "ansible-playbook", "-i",  "hosts", playbook.getFilename());
        //ProcessBuilder processBuilder = new ProcessBuilder("wsl", "ansible-playbook",  "-i",  "hosts", "interfaces_facts.yaml");
        if (PLAYBOOKS_NAMES.contains(playbook.getFilename())) {
            processBuilder.directory(ANSIBLE_DIR);
        }
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

    private String choosePlaybook(String playbookName) {
        //String command = "bash -c \"/home/grigor/.local/bin/ansible-playbook -i hosts ";
        String[] command = new String[]{"ansible-playbook",  "-i",  "hosts"};
        //String playbook = dataService.getPlaybooksNames().get(playbookId) + "\"";
        //String[] command2 = new String[]{"ansible-playbook",  "-i",  "hosts", "interfaces_facts.yaml"};
        System.out.println(Arrays.toString(command));
        return Arrays.toString(command) + playbookName + "\"";
    }

    public Map<String, String> loadPlaybook(Integer playbookId) throws IOException, InterruptedException {
        String playbook = dataService.getPlaybooksNames().get(playbookId) + "\"";
        String bashCommand = "bash -c \"cat " + playbook;
        StringBuilder outputBuffer = new StringBuilder();
        executeCommand(outputBuffer, bashCommand);

        Map<String, String> response = new HashMap<>();
        response.put("output", outputBuffer.toString());
        response.put("exitCode", "0");
        return response;
    }

    public void updatePlaybook(String playbookName, String content) throws IOException {
        List<String> lines = List.of(content.split("\n"));
        Files.write(Paths.get(ANSIBLE_DIR.getAbsolutePath() + File.separator + playbookName), lines);
//        Files.write(Paths.get(ANSIBLE_DIR.getAbsolutePath() + File.separator
//                + dataService.getPlaybooksNames().get(playbookId)), lines);
    }

    public Object getHostsFile() throws IOException, InterruptedException {
        String playbook = "hosts" + "\"";
        String bashCommand = "bash -c \"cat " + playbook;
        StringBuilder outputBuffer = new StringBuilder();
        executeCommand(outputBuffer, bashCommand);

        Map<String, String> response = new HashMap<>();
        response.put("output", outputBuffer.toString());
        response.put("exitCode", "0");
        return response;
    }

    public void updateHostsFile(String content) throws IOException {
        List<String> lines = List.of(content.split("\n"));
        Files.write(Paths.get(ANSIBLE_DIR.getAbsolutePath() + File.separator + "hosts"), lines);
    }

    private void executeCommand(StringBuilder outputBuffer, String bashCommand) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", bashCommand);
        processBuilder.directory(AnsibleService.ANSIBLE_DIR);
        processBuilder.redirectErrorStream(true);
        // Start the process
        Process process = processBuilder.start();
        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            outputBuffer.append(line).append("\n");

        // Wait for the process to complete
        process.waitFor();
    }
}
