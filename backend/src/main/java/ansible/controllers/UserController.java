package ansible.controllers;

import ansible.model.User;
import ansible.services.UserService;
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
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User u = userService.loadUser(user);

        if (u == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(u);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User u = userService.saveUser(user);

        if (u == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else
            return ResponseEntity.status(HttpStatus.OK).build();
    }

}
