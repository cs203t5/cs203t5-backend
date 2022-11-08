package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.SecurityUser;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.UsersDTO;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user1;
        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            user1 = usersRepository.findByUsername(jwt.getSubject()).orElse(null);
        } else if (auth.getPrincipal() instanceof SecurityUser) {
            SecurityUser user = (SecurityUser) auth.getPrincipal();
            user1 = usersRepository.findByUsername(user.getUsername()).orElse(null);
        } else {
            String user = (String) auth.getPrincipal();
            log.info("user is" + user);
            user1 = usersRepository.findByUsername(user).orElse(null);
        }

        return user1;
    }

    public UsersDTO saveUser(Users user) {
        log.info("Saving new user to the database");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role consumer = roleRepository.findByName("CONSUMER");
        user.setRoles(consumer);
        Users saveUser = usersRepository.save(user);

        return saveUser.convertToDTO();
    }

    public String getRole() {
        Users user = getCurrentUser();
        return user.getRoles().getName();
    }

    public UsersDTO updateUser(Users user) {
        log.info("Updating user info");
        return usersRepository.save(user).convertToDTO();
    }

    public UsersDTO upgradeRole(String username) {
        Users user = usersRepository.findByUsername(username).orElseThrow();
        Role role = roleRepository.findByName("BUSINESS");
        user.setRoles(role);
        return usersRepository.save(user).convertToDTO();
    }

    public UsersDTO downgradeRole(String username) {
        Users user = usersRepository.findByUsername(username).orElseThrow();
        Role role = roleRepository.findByName("CONSUMER");
        user.setRoles(role);
        usersRepository.save(user);
        return user.convertToDTO();
    }

    public List<UsersDTO> getUsers() {
        return usersRepository.findAll().stream().map(Users::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsersDTO getUser(String username) {
        return usersRepository.findByUsername(username).orElseThrow().convertToDTO();
    }
}
