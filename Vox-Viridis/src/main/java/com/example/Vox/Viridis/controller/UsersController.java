package com.example.Vox.Viridis.controller;

import java.net.URI;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.UsersDTO;
import com.example.Vox.Viridis.service.UsersService;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/name")
    public String getName(Principal principal) {
        return "Hello, " + principal.getName();
    }

    @PostMapping("/save")
    public ResponseEntity<UsersDTO> saveUser(@RequestBody Users user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(usersService.saveUser(user));
    }
}
