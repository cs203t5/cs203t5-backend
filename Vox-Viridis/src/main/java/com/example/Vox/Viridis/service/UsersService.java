package com.example.Vox.Viridis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Vox.Viridis.model.Role;
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

    public UsersDTO saveUser(Users user) {
        log.info("Saving new user to the database");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> roles = new ArrayList<>();
        Role consumer = roleRepository.findByName("CONSUMER");
        roles.add(consumer);
        user.setRoles(roles);
        Users saveUser = usersRepository.save(user);
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setUsername(saveUser.getUsername());
        usersDTO.setAccount_id(saveUser.getAccount_id());
        return usersDTO;
    }
    public List<Users> findAll() {
        log.info("retrieving all users");
        return usersRepository.findAll();
    }

    public Optional<Users> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return usersRepository.findByUsername(username);
    }

    public Optional<Users> findById(Long id) {
        log.info("retrieving user {}", id);
        return usersRepository.findById(id);
    }
}
