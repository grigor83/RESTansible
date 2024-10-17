package mtel.controllers;

import jakarta.validation.Valid;
import mtel.models.User;
import mtel.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserService.LoginRequest loginRequest) {
        User user = userService.loginUser(loginRequest);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        System.out.println(user);
        userService.registerUser(user);
        return ResponseEntity.ok().body(HttpStatus.ACCEPTED);
    }
}
