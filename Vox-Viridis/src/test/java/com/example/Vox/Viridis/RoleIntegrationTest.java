package com.example.Vox.Viridis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.RoleDTO;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RoleIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private UsersRepository users;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        // clear the database after each test
        users.deleteAll();
        roles.deleteAll();
    }

    @BeforeEach
    void createAdminAccount() {
        users.deleteAll();
        roles.deleteAll();

        Role role = new Role();
        role.setRoleId(1L);
        role.setName("CONSUMER");
        Role role2 = new Role();
        role2.setRoleId(2L);
        role2.setName("ADMIN");

        roles.save(role);
        roles.save(role2);

        Users user = new Users();
        user.setUsername("admin2");
        user.setEmail("admin2@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        Role consumer = roles.findByName("CONSUMER");
        user.setRoles(consumer);
        users.save(user);

        Users user2 = new Users();
        user2.setUsername("admin1");
        user2.setEmail("admin1@test.com");
        user2.setFirstName("Admin");
        user2.setLastName("admin");
        user2.setPassword(passwordEncoder.encode("goodpassword"));
        Role admin = roles.findByName("ADMIN");
        user2.setRoles(admin);
        users.save(user2);
    }

    private TestRestTemplate authenticatedRestTemplate() {
        return restTemplate.withBasicAuth("admin1", "goodpassword");
    }

    @Test
    void getRoles_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/role");
        ResponseEntity<List<RoleDTO>> result = authenticatedRestTemplate().exchange(uri,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<RoleDTO>>() {});
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
    }

    @Test
    void getRoleByName_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/role/CONSUMER");
        ResponseEntity<RoleDTO> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET,
                null, new ParameterizedTypeReference<RoleDTO>() {});
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
    }

    @Test
    void getRoleByName_NotFound() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/role/EMPTY");
        ResponseEntity<RoleDTO> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET,
                null, new ParameterizedTypeReference<RoleDTO>() {});
        assertEquals(200, result.getStatusCode().value());
        assertNull(result.getBody());
    }

    @Test
    void createRote_ValidRole() throws URISyntaxException {
        URI uri = new URI(baseUrl + port + "/api/role");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Role role = new Role();
        role.setName("TEST");

        ResponseEntity<RoleDTO> result =
                authenticatedRestTemplate().postForEntity(uri, role, RoleDTO.class);

        assertEquals(201, result.getStatusCode().value());
        RoleDTO roleResult = result.getBody();
        assertNotNull(roleResult);
        assertEquals("TEST", roleResult.getName());
    }

    @Test
    void createRote_InvalidRole() throws URISyntaxException {
        URI uri = new URI(baseUrl + port + "/api/role");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        ResponseEntity<RoleDTO> result =
                authenticatedRestTemplate().postForEntity(uri, null, RoleDTO.class);

        assertEquals(400, result.getStatusCode().value());
    }

}
