package com.example.Vox.Viridis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void saveRole_ValidRole_ReturnRole() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");

        when(roleRepository.save(role)).thenReturn(role);

        Role roleRet = roleRepository.save(role);

        assertNotNull(roleRet);
        verify(roleRepository).save(role);
    }

    @Test
    void saveRole_DuplicateRole_ReturnNull() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("TEST");

        Role role2 = new Role();
        role2.setRoleId(1l);
        role2.setName("TEST");

        when(roleRepository.save(role2)).thenReturn(null);
        Role roleRet2 = roleRepository.save(role2);

        assertNull(roleRet2);
        verify(roleRepository).save(role2);
    }

    @Test
    void getRoles_ValidRole_ReturnRole() {
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");
        roles.add(role);

        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> roleRet = roleRepository.findAll();
        assertNotNull(roleRet);
        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById_ValidId_ReturnRole() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");

        when(roleRepository.findById(1l)).thenReturn(java.util.Optional.of(role));

        Role roleRet = roleRepository.findById(1l).get();
        assertNotNull(roleRet);
        verify(roleRepository).findById(1l);
    }

    @Test
    void getRoleById_InvalidId_ReturnNull() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");

        when(roleRepository.findById(2l)).thenReturn(null);

        Optional<Role> roleRet = roleRepository.findById(2l);
        assertNull(roleRet);
        verify(roleRepository).findById(2l);
    }

    @Test
    void updateRole_ValidRole_ReturnRole() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");

        when(roleRepository.findById(1l)).thenReturn(java.util.Optional.of(role));

        Role roleToUpdate = roleRepository.findById(1l).get();
        roleToUpdate.setName("TEST");
        when(roleRepository.save(roleToUpdate)).thenReturn(roleToUpdate);
        Role roleRet = roleRepository.save(roleToUpdate);
        assertNotNull(roleRet);
        assertEquals(roleRet.getName(), "TEST");
        verify(roleRepository).findById(1l);
        verify(roleRepository).save(roleToUpdate);
    }

    @Test
    void updateRole_InvalidId_ReturnNull() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");

        when(roleRepository.findById(2l)).thenReturn(null);

        Optional<Role> roleRet = roleRepository.findById(2l);
        assertNull(roleRet);
        verify(roleRepository).findById(2l);
    }

    @Test
    void deleteRole_ValidId_ReturnNone() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");

        when(roleRepository.findById(1l)).thenReturn(java.util.Optional.of(role));
        Optional<Role> roleToDelete = roleRepository.findById(1l);
        roleRepository.deleteById(1l);
        assertNotNull(roleToDelete);
        verify(roleRepository).findById(1l);
        verify(roleRepository).deleteById(1l);
    }

    @Test
    void deleteRole_InvalidId_ReturnNone() {
        Role role = new Role();
        role.setRoleId(1l);
        role.setName("ADMIN");

        when(roleRepository.findById(2l)).thenReturn(null);

        Optional<Role> roleRet = roleRepository.findById(2l);
        assertNull(roleRet);
        verify(roleRepository).findById(2L);
    }
}

