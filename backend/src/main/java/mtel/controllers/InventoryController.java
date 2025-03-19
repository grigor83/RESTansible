package mtel.controllers;

import mtel.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<UrlResource> getFile(@PathVariable String filename) throws MalformedURLException {
        UrlResource file = inventoryService.loadFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getInventories(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(inventoryService.loadInventories(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<String> uploadInventory(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId) {
        try {
            String result = inventoryService.saveFile(file, userId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PutMapping()
    public ResponseEntity<String> updateInventory(@RequestParam("file") MultipartFile file, @RequestParam("inventoryId") int inventoryId) {
        try{
            String result = inventoryService.updateFile(file, inventoryId);
            return ResponseEntity.ok(result);
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/{userId}/data")
    public ResponseEntity<?> getHostsAndPlaybooks(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(inventoryService.loadHostsNames(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}
