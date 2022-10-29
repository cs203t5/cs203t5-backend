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
    private RoleRepository roleRepository;

    public List<RoleDTO> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(role -> role.convertToDTO()).collect(Collectors.toList());
    }

    public RoleDTO getRoleById(long id) {
        return roleRepository.findById(id).get().convertToDTO();
    }

    public RoleDTO createRole(Role role) {
        return roleRepository.save(role).convertToDTO();
    }

    public RoleDTO updateRole(Role role) {
        Role currentRole = roleRepository.findById(role.getRoleId()).get();
        currentRole.setName(role.getName());
        return roleRepository.save(currentRole).convertToDTO();
    }

    public boolean deleteRole(long id) {
        try {
            roleRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
