package com.example.Vox.Viridis.controller;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/users")
public class UsersController {
    @GetMapping("/name")
    public String getName(Principal principal) {
        return "Hello, " + principal.getName();
    }
}
