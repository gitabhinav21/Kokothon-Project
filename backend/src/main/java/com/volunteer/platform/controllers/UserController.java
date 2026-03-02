package com.volunteer.platform.controllers;

import com.volunteer.platform.models.User;
import com.volunteer.platform.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserProfile(email));
    }

    @PutMapping("/update-skills")
    public ResponseEntity<User> updateSkills(@RequestBody List<String> skills, Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateSkills(email, skills));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserApplications(email));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }
}
