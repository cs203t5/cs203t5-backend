package com.example.Vox.Viridis;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import com.example.Vox.Viridis.model.Products;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.ProductsDTO;
import com.example.Vox.Viridis.repository.ProductsRepository;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductsIntegrationTest {
        @LocalServerPort
        private int port;

        private final String baseUrl = "http://localhost:";

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private ProductsRepository products;

        @Autowired
        private UsersRepository users;

        @Autowired
        private RoleRepository roles;

        @Autowired
        private PasswordEncoder passwordEncoder;


        @AfterEach
        void tearDown() {
                // clear the database after each test
                products.deleteAll();
        }

        @BeforeEach
        void createAccount() {
                users.deleteAll();
                roles.deleteAll();

                Role role = new Role(1l, "ADMIN", null);
                role = roles.save(role);

                Role roleConsumer = new Role((long)2l, "CONSUMER", null);
                roleConsumer = roles.save(roleConsumer);

                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin12@test.com");
                admin.setFirstName("Admin");
                admin.setLastName("admin");
                admin.setPassword(passwordEncoder.encode("goodpassword"));
                admin.setRoles(role);
                admin = users.save(admin);

                Users consumer = new Users();
                consumer.setUsername("consumer");
                consumer.setEmail("consumer12@test.com");
                consumer.setFirstName("Consumer");
                consumer.setLastName("consumer");
                consumer.setPassword(passwordEncoder.encode("goodpassword"));
                consumer.setRoles(roleConsumer);
                consumer = users.save(consumer);
        }


        private String getJwtToken() {
                ResponseEntity<String> tokenResponse = restTemplate.withBasicAuth("admin", "goodpassword")
                                .postForEntity(baseUrl + port + "/api/users/token", null, String.class);
                return tokenResponse.getBody();
        }
        private String getJwtTokenConsumer() {
                ResponseEntity<String> tokenResponse = restTemplate.withBasicAuth("consumer", "goodpassword")
                                .postForEntity(baseUrl + port + "/api/users/token", null, String.class);
                return tokenResponse.getBody();
        }

        private TestRestTemplate authenticatedRestTemplate() {
                /*String jwtToken = getJwtToken();

                restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
                        request.getHeaders().add("Authorization", "Bearer " + jwtToken);
                        return execution.execute(request, body);
                });
                return restTemplate;*/

                // Using Basic Authentiaction
                return restTemplate.withBasicAuth("admin", "goodpassword");
        }

        private TestRestTemplate authenticatedRestTemplateConsumer() {
                /*String jwtToken = getJwtTokenConsumer();

                restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
                        request.getHeaders().add("Authorization", "Bearer " + jwtToken);
                        return execution.execute(request, body);
                });
                return restTemplate;*/

                // Using Basic Authentiaction
                return restTemplate.withBasicAuth("consumer", "goodpassword");
        }

        private Users getUser() {
                return users.findByUsername("admin").get();
        }

        private Users getConsumer() {
                return users.findByUsername("Consumer").get();
        }

        private void modifyProductList(List<Products> productsList) {
                productsList.forEach(c -> {
                        c.setCreatedBy(null);
                });
        }

        @Test
        public void getAllProducts_Success() throws Exception {
                Users user = getUser();
                URI uri = new URI(baseUrl + port + "/api/products");

                Products product = new Products();
                product.setName("This is a new product");
                product.setDescription("This is a new product");
                product.setCreatedBy(user);

                Products product2 = new Products();
                product2.setName("This is a new new product");
                product2.setDescription("This is a new new product");
                product2.setCreatedBy(user);

                List<Products> productsList = new ArrayList<Products>();
                productsList.add(product);
                productsList.add(product2);

                productsList = products.saveAll(productsList);
                ResponseEntity<List<Products>> result = restTemplate.exchange(uri,
                                HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<Products>>() {
                                });
                assertEquals(200, result.getStatusCode().value());
                modifyProductList(productsList);
                assertEquals(productsList, result.getBody());
        }

        @Test
        public void getProductsById_Success() throws Exception {
                Users user = getUser();
                URI uri = new URI(baseUrl + port + "/api/products");
                Products product = new Products();
                product.setName("This is a new product");
                product.setDescription("This is a new product");
                product.setCreatedBy(user);

                Products product2 = new Products();
                product2.setName("This is a new new product");
                product2.setDescription("This is a new new product");
                product2.setCreatedBy(user);

                List<Products> productsList = new ArrayList<Products>();
                productsList.add(product);
                productsList.add(product2);

                productsList = products.saveAll(productsList);
                ResponseEntity<Products> result = restTemplate.exchange(uri + "/" + product2.getId(),
                                HttpMethod.GET, null, Products.class);
                assertEquals(200, result.getStatusCode().value());
                modifyProductList(productsList);
                assertEquals(product2, result.getBody());
        }

        @Test
        public void getProductsId_NotFound_Fail() throws Exception {
                URI uri = new URI(baseUrl + port + "/api/products/12");
                ResponseEntity<Products> result = restTemplate.exchange(uri,
                                HttpMethod.GET, null, Products.class);
                assertEquals(404, result.getStatusCode().value());
                Products productsResult = result.getBody();
                assertNull(productsResult == null ? result : productsResult.getName());
        }

        @Test
        public void addProducts_Success() throws Exception {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                URI uri = new URI(baseUrl + port + "/api/products");
                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("name", "This is a new Product");
                map.add("description", "This is a new Product");

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,headers);
                ResponseEntity<Products> result = authenticatedRestTemplate().postForEntity(uri,
                request, Products.class);
                assertEquals(201, result.getStatusCode().value());
                Products product = result.getBody();
                assertNotNull(product);
                assertEquals("This is a new Product", product.getName());
                assertEquals("This is a new Product",product.getDescription());
        }       

        @Test
        public void updateProducts_Success() throws Exception{
                Users user = getUser();
                URI uri = new URI(baseUrl + port + "/api/products");
                Products product = new Products();
                product.setName("This is a new product");
                product.setDescription("This is a new product");
                product.setCreatedBy(user);
                products.save(product);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("name", "This is a new product 2");
                map.add("description", "This is a new product 2");
                
                HttpEntity<MultiValueMap<String, String>> request =
                                new HttpEntity<MultiValueMap<String, String>>(map, headers);

                ResponseEntity<Products> result =
                                authenticatedRestTemplate().exchange(uri + "/" + product.getId(),
                                                HttpMethod.PUT, request, Products.class);
                assertEquals(200, result.getStatusCode().value());
                Products results = result.getBody();
                assertNotNull(results);
                assertEquals("This is a new product 2", results.getName());
                assertEquals("This is a new product 2", results.getDescription());
        }

        @Test
        public void updateProducts_InvalidId_Fail() throws Exception {
                URI uri = new URI(baseUrl + port + "/api/products/12");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("name", "This is a new Product");
                map.add("description", "This is a new Product");

                HttpEntity<MultiValueMap<String, String>> request =
                                 new HttpEntity<MultiValueMap<String, String>>(map, headers);

                 ResponseEntity<Products> result = authenticatedRestTemplate().exchange(uri,
                                 HttpMethod.PUT, request, Products.class);
                 assertEquals(404, result.getStatusCode().value());

        }

        @Test
        public void deleteProducts_Success() throws Exception{
                Users user = getUser();
                URI uri = new URI(baseUrl + port + "/api/products");
                Products product = new Products();
                product.setName("This is a new product");
                product.setDescription("This is a new product");
                product.setCreatedBy(user);
                
                products.save(product);

                ResponseEntity<Void> result = authenticatedRestTemplate().exchange(
                                uri + "/" + product.getId(), HttpMethod.DELETE, null, Void.class);
                assertEquals(200, result.getStatusCode().value());
                assertTrue(products.findById(product.getId()).isEmpty());
        }

        @Test
        public void deleteProducts_InvalidId_Fail() throws Exception{
                URI uri = new URI(baseUrl + port + "/api/products/12");

                ResponseEntity<Void> result = authenticatedRestTemplate().exchange(uri,
                                HttpMethod.DELETE, null, Void.class);
                assertEquals(404, result.getStatusCode().value());
        }

        @Test 
        public void buyProducts_Success() throws Exception{
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                URI uri = new URI(baseUrl + port + "/api/products/buy");
                Products product = new Products();
                product.setName("New product");
                product.setDescription("This is a new product");
                product.setCategory("This is a new product");
                product.setPoint(10);
                product.setCreatedBy(getUser());
                products.save(product);

                Users consumer = getConsumer();
                consumer.setPoints(80);
                users.save(consumer);

                MultiValueMap<String, Integer> map = new LinkedMultiValueMap<String, Integer>();
                map.add("point", 70);

                HttpEntity<MultiValueMap<String, Integer>> request = new HttpEntity<MultiValueMap<String, Integer>>(map,headers);
                ResponseEntity<Users> result = authenticatedRestTemplateConsumer().exchange(uri + "/"+  product.getId(),
                                 HttpMethod.PUT, request, Users.class);
                assertEquals(200, result.getStatusCode().value());
                assertEquals(70, getConsumer().getPoints());
        }

        @Test
        public void buyProducts_InvalidId_Fail() throws Exception{
                URI uri = new URI(baseUrl + port + "/api/products/buy/12");
                Users consumer = getConsumer();
                consumer.setPoints(80);
                users.save(consumer);
                ResponseEntity<Users> result = authenticatedRestTemplate().exchange(uri,
                                HttpMethod.PUT, null, Users.class);
                assertEquals(404, result.getStatusCode().value());
        }

        @Test
        public void buyProducts_InsufficientPoint_ReturnErrorMessage() throws Exception {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                URI uri = new URI(baseUrl + port + "/api/products/buy");
                Products product = new Products();
                product.setName("New product");
                product.setDescription("This is a new product");
                product.setCategory("This is a new product");
                product.setPoint(10);
                product.setCreatedBy(getUser());
                products.save(product);
                Users consumer = getConsumer();
                consumer.setPoints(5);
                users.save(consumer);
                ResponseEntity<Users> result = authenticatedRestTemplateConsumer().exchange(uri + "/"+  product.getId(),
                                 HttpMethod.PUT, null, Users.class);
                assertEquals(400, result.getStatusCode().value());
                assertEquals(5, consumer.getPoints());
        }

        }
