package mtel.controllers;

import mtel.services.AnsibleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/ansible")
public class AnsibleController {

    private final AnsibleService ansibleService;

    public AnsibleController(AnsibleService ansibleService) {
        this.ansibleService = ansibleService;
    }

    @GetMapping("/{playbookId}")
    public ResponseEntity<?> runPlaybook(@PathVariable Integer playbookId) {
        try {
            return ResponseEntity.ok().body(ansibleService.runPlaybook(playbookId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/playbooks/{playbookId}")
    public ResponseEntity<?> loadPlaybook(@PathVariable Integer playbookId) {
        try {
            return ResponseEntity.ok().body(ansibleService.loadPlaybook(playbookId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/playbooks/{playbookId}")
    public ResponseEntity<?> updatePlaybook(@PathVariable Integer playbookId,
                                            @RequestBody String content) throws IOException, InterruptedException {
        ansibleService.updatePlaybook(playbookId, content);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hosts")
    public ResponseEntity<?> loadHostsFile() throws IOException, InterruptedException {
        return ResponseEntity.ok().body(ansibleService.getHostsFile());
    }

    @PutMapping("/hosts")
    public ResponseEntity<?> updateHostsFile(@RequestBody String content) throws IOException, InterruptedException {
        ansibleService.updateHostsFile(content);
        return ResponseEntity.ok().build();
    }

}
