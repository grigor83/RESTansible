package ansible.controllers;

import ansible.services.PlaybookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/playbooks")
public class PlaybookController {

    private final PlaybookService playbookService;
    @Autowired
    public PlaybookController(PlaybookService playbookService) {
        this.playbookService = playbookService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPlaybooks(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(playbookService.getPlaybooks(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/content/{playbookId}")
    public ResponseEntity<?> getPlaybookContent(@PathVariable Integer playbookId) {
        try {
            return ResponseEntity.ok().body(playbookService.loadPlaybookContent(playbookId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{playbookID}")
    public ResponseEntity<String> updatePlaybook(@PathVariable Integer playbookID, @RequestBody String content) {
        try{
            String result = playbookService.updateFile(playbookID, content);
            return ResponseEntity.ok().body(result);
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createPlaybook(@RequestBody PlaybookRequest request) {
        try {
            return ResponseEntity.ok(playbookService.createFile(request.userId, request.filename, request.content));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{playbookId}")
    public ResponseEntity<?> deletePlaybook(@PathVariable Integer playbookId) {
        try {
            return ResponseEntity.ok().body(playbookService.deletePlaybook(playbookId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

class PlaybookRequest {
    public Integer userId;
    public String filename;
    public String content;
}