package com.example.Vox.Viridis.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UsersService usersService;

    @Mock
    private RoleRepository roleService;;

    @Mock
    private UsersRepository usersRepository;

    @Test
    void saveUser_ValidUsers_ReturnUser() {
        Users user = new Users();
        user.setAccountId(1l);
        user.setEmail("users@voxviridis.com");
        user.setFirstName("Vox");
        user.setLastName("Viridis");
        user.setUsername("vv5");

        when(usersRepository.save(user)).thenReturn(user);

        Users userRet = usersRepository.save(user);

        assertNotNull(userRet);
        verify(usersRepository).save(user);

    }

    @Test
    void saveUser_DuplicateEmail_ReturnNull() {
        Users user = new Users();
        user.setAccountId(1l);
        user.setEmail("users@voxviridis.com");
        user.setFirstName("Vox");
        user.setLastName("Viridis");
        user.setUsername("vv5");

        when(usersRepository.save(user)).thenReturn(user);

        usersRepository.save(user);

        Users user2 = new Users();
        user2.setAccountId(1l);
        user2.setEmail("users@voxviridis.com");
        user2.setFirstName("Vox");
        user2.setLastName("Viridis");
        user2.setUsername("vv6");

        when(usersRepository.save(user2)).thenReturn(null);

        Users userRet = usersRepository.save(user2);
        assertNull(userRet);
        verify(usersRepository).save(user);
        verify(usersRepository).save(user2);
    }

    @Test
    void saveUser_DuplicateUsername_ReturnUser() {
        Users user = new Users();
        user.setAccountId(1l);
        user.setEmail("users@voxviridis.com");
        user.setFirstName("Vox");
        user.setLastName("Viridis");
        user.setUsername("vv5");
        when(usersRepository.save(user)).thenReturn(user);

        usersRepository.save(user);

        Users user2 = new Users();
        user2.setAccountId(1l);
        user2.setEmail("users2@voxviridis.com");
        user2.setFirstName("Vox");
        user2.setLastName("Viridis");
        user2.setUsername("vv5");
        when(usersRepository.save(user2)).thenReturn(null);

        Users userRet = usersRepository.save(user2);
        assertNull(userRet);
        verify(usersRepository).save(user);
        verify(usersRepository).save(user2);

    }
}
