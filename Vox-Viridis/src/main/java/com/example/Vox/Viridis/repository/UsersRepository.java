package com.example.Vox.Viridis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Vox.Viridis.model.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
}
