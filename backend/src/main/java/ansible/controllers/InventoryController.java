package ansible.controllers;

import ansible.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping("/{userId}/names")
    public ResponseEntity<?> getFileNames(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(inventoryService.getFileNames(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getInventories(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(inventoryService.getInventories(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/content/{inventoryId}")
    public ResponseEntity<?> getInventoryContent(@PathVariable Integer inventoryId) {
        try {
            return ResponseEntity.ok().body(inventoryService.loadInventoryContent(inventoryId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<String> updateInventory(@PathVariable Integer inventoryId, @RequestBody String content) {
        try{
            String result = inventoryService.updateFile(inventoryId, content);
            return ResponseEntity.ok().body(result);
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createInventory(@RequestBody PlaybookRequest request) {
        try {
            return ResponseEntity.ok(inventoryService.createFile(request.userId, request.filename, request.content));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<?> deletePlaybook(@PathVariable Integer inventoryId) {
        try {
            return ResponseEntity.ok().body(inventoryService.deleteInventory(inventoryId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
