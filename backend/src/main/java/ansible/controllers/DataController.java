package ansible.controllers;

import ansible.services.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/data")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/devices")
    public ResponseEntity<?> getDevicesNames() {
        return ResponseEntity.ok().body(dataService.getDevicesNames());
    }

    @GetMapping("/playbooks")
    public ResponseEntity<?> getPlaybooksNames() {
        return ResponseEntity.ok().body(dataService.getPlaybooksNames());
    }

}
