package com.example.Vox.Viridis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.UsersDTO;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jwt.SignedJWT;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UsersIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roles;

    @BeforeEach
    void createRoles() {
        users.deleteAll();
        roles.deleteAll();

        Role consumer = new Role(1L, "CONSUMER", null);
        Role business = new Role(2L, "BUSINESS", null);
        Role admin = new Role(3L, "ADMIN", null);

        roles.save(consumer);
        roles.save(business);
        roles.save(admin);
    }

    @BeforeEach
    void createAdminAccount() {

        Users user = new Users();
        user.setUsername("admin2");
        user.setEmail("admin2@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        Role consumer = roles.findByName("CONSUMER");
        user.setRoles(consumer);
        users.save(user);

    }

    private TestRestTemplate authenticatedRestTemplate() {
        return restTemplate.withBasicAuth("admin2", "goodpassword");
    }

    @Test
    public void getName_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/name");

        ResponseEntity<String> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET,
                null, new ParameterizedTypeReference<String>() {});
        assertEquals(200, result.getStatusCode().value());
        assertEquals("admin2", result.getBody());
    }

    @Test
    public void getName_Unauthorized() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/name");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "goodpassword")
                .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {});
        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    public void getRole_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/role");

        ResponseEntity<String> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET,
                null, new ParameterizedTypeReference<String>() {});
        assertEquals(200, result.getStatusCode().value());
        assertEquals("CONSUMER", result.getBody());
    }

    @Test
    public void getRole_Unauthorized() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/role");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "goodpassword")
                .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {});
        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    public void saveUser_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/save");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Users user = new Users();
        user.setUsername("admin3");
        user.setEmail("admin3@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));

        ResponseEntity<UsersDTO> result =
                authenticatedRestTemplate().postForEntity(uri, user, UsersDTO.class);

        assertEquals(201, result.getStatusCode().value());
        assertEquals("admin3", result.getBody().getUsername());
    }

    @Test
    public void saveUser_duplicateUsername_badRequest() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/save");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Users user = new Users();
        user.setUsername("admin2");
        user.setEmail("admin3@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));

        ResponseEntity<UsersDTO> result =
                authenticatedRestTemplate().postForEntity(uri, user, UsersDTO.class);

        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void saveUser_duplicateEmail_badRequest() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/save");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Users user = new Users();
        user.setUsername("admin3");
        user.setEmail("admin2@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));

        ResponseEntity<UsersDTO> result =
                authenticatedRestTemplate().postForEntity(uri, user, UsersDTO.class);

        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void getToken_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/token");

        ResponseEntity<String> result =
                authenticatedRestTemplate().postForEntity(uri, null, String.class);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
    }

    @Test
    public void getToken_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/users/save");


        ResponseEntity<String> result =
                authenticatedRestTemplate().postForEntity(uri, null, String.class);

        assertEquals(400, result.getStatusCode().value());
    }
}
