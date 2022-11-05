package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.dto.RoleDTO;
import com.example.Vox.Viridis.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<RoleDTO> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(role -> role.convertToDTO()).collect(Collectors.toList());
    }

    public RoleDTO getRoleByName(String name) {
        try {
            Role role = roleRepository.findByName(name);
            return role.convertToDTO();
        } catch (Exception e) {
            log.info("Role with name " + name + " was not found");
            return null;
        }
    }

    public RoleDTO createRole(Role role) {
        return roleRepository.save(role).convertToDTO();
    }

    public RoleDTO updateRole(String name, Role role) {
        Role currentRole = roleRepository.findByName(name);
        currentRole.setName(role.getName());
        return roleRepository.save(currentRole).convertToDTO();
    }

    public boolean deleteRole(String name) {
        try {
            Role role = roleRepository.findByName(name);
            roleRepository.deleteById(role.getRoleId());
            log.info("Role with name " + name + " was deleted");
            return true;
        } catch (Exception e) {
            log.info("Role with name " + name + " was not deleted");
            return false;
        }
    }
}
