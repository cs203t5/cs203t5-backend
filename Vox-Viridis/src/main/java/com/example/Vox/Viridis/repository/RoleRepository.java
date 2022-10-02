package com.example.Vox.Viridis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Vox.Viridis.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
