package ansible.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {

    private List<String> devicesNames;
    private List<String> playbooksNames;

    @PostConstruct
    public void initData() throws IOException, InterruptedException {
        devicesNames = new ArrayList<>();
        loadDevicesNames();

        playbooksNames = new ArrayList<>();
        listPlaybookFiles();
    }

    private void loadDevicesNames() throws IOException, InterruptedException {
        String bashCommand = "bash -c \"awk '/ansible_host=/ {print $1}' hosts\"";
        executeCommand(devicesNames, bashCommand);
    }

    private void listPlaybookFiles() throws IOException, InterruptedException {
        String bashCommand = "bash -c \"ls | grep '\\.yaml$'\"";
        executeCommand(playbooksNames, bashCommand);
    }

    private void executeCommand(List<String> lists, String bashCommand) throws IOException, InterruptedException {
        // Create a process builder
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", bashCommand);
        processBuilder.directory(AnsibleService.ANSIBLE_DIR);
        processBuilder.redirectErrorStream(true);
        // Start the process
        Process process = processBuilder.start();
        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            lists.add(line);
        // Wait for the process to complete
        process.waitFor();
    }

    public List<String> getDevicesNames() {
        return devicesNames;
    }

    public List<String> getPlaybooksNames() {
        return playbooksNames;
    }

}
