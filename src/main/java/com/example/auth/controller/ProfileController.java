package com.example.auth.controller;

import com.example.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<String> profile() {
        return ResponseEntity.ok("Welcome to your profile!");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id){

        String user = userService.DeleteById(id);

        return ResponseEntity.ok(user);

    }
}