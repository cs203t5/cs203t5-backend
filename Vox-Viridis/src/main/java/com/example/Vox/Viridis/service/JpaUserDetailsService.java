package com.example.Vox.Viridis.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.Vox.Viridis.model.SecurityUser;
import com.example.Vox.Viridis.repository.UsersRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UsersRepository userRepository;

    public JpaUserDetailsService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(SecurityUser::new).orElseThrow(
                () -> new UsernameNotFoundException("Username not found: " + username));
    }
}
