package com.example.Vox.Viridis.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.UsersDTO;
import com.example.Vox.Viridis.service.TokenService;
import com.example.Vox.Viridis.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final UsersService usersService;
    private final TokenService tokenService;

    @GetMapping()
    public List<UsersDTO> getUsers() {
        return usersService.getUsers();
    }

    @GetMapping("/name")
    public ResponseEntity<String> getName(Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }

    @PostMapping("/save")
    public ResponseEntity<UsersDTO> saveUser(@Valid @RequestBody Users user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/user/save").toUriString());
        try {
            UsersDTO userDto = usersService.saveUser(user);
            return ResponseEntity.created(uri).body(userDto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/token")
    public ResponseEntity<String> token(Authentication authentication) {
        log.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        log.debug("Token granted {}", token);

        return ResponseEntity.ok(token);
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole(Principal principal) {
        return ResponseEntity.ok(usersService.getRole());
    }

    @PutMapping("/role/upgrade/{username}")
    public ResponseEntity<UsersDTO> upgradeRole(@PathVariable String username) {
        return ResponseEntity.ok(usersService.upgradeRole(username));
    }

    @PutMapping("/role/downgrade/{username}")
    public ResponseEntity<UsersDTO> downgradeRole(@PathVariable String username) {
        return ResponseEntity.ok(usersService.downgradeRole(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UsersDTO> getUserById(@PathVariable String username) {
        return ResponseEntity.ok(usersService.getUser(username));
    }
}
