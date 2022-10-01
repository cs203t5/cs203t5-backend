package com.example.Vox.Viridis.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Vox.Viridis.model.SecurityUser;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.UsersDTO;
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

    // @Override
    // public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Optional<Users> user = usersRepository.findByUsername(username);

    // if (user == null) {
    // log.error("User not found: " + username);
    // throw new UsernameNotFoundException("User not found");
    // } else {
    // log.info("User found: " + username);
    // }
    // Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    // authorities.add(new SimpleGrantedAuthority("user"));
    // return new org.springframework.security.core.userdetails.User(user.getUsername(),
    // user.getPassword(), authorities);
    // }

    public UsersDTO saveUser(Users user) {
        log.info("Saving new user to the database");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users saveUser = usersRepository.save(user);
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setUsername(saveUser.getUsername());
        usersDTO.setAccount_id(saveUser.getAccount_id());
        return usersDTO;
    }
}
