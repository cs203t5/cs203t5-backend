package com.example.Vox.Viridis.service;

import javax.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.SecurityUser;
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
        if (auth == null)
            return null;
        return usersRepository.findByUsername(((SecurityUser) auth.getPrincipal()).getUsername())
                .orElse(null);
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
}
