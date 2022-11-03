package com.example.Vox.Viridis.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.dto.RoleDTO;
import com.example.Vox.Viridis.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public List<RoleDTO> getRoles() {
        return roleService.getRoles();
    }

    @GetMapping("{name}")
    public RoleDTO getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name);
    }

    @PostMapping
    public RoleDTO createRole(@Valid Role role) {
        return roleService.createRole(role);
    }

    @PutMapping("{id}")
    public RoleDTO updateRole(@PathVariable long id, @Valid Role role) {
        return roleService.updateRole(role);
    }

    @DeleteMapping("{id}")
    public void deleteRole(@PathVariable long id) {
        boolean isDeleted = roleService.deleteRole(id);
        if (isDeleted) {
            log.info("Role with id " + id + " was deleted");
        } else {
            log.info("Role with id " + id + " was not deleted");
        }
    }
}
