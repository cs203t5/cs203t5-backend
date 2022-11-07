package com.example.Vox.Viridis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.model.ParticipationAddPointInputModel;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.RewardType;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.PaginationDTO;
import com.example.Vox.Viridis.repository.CampaignRepository;
import com.example.Vox.Viridis.repository.ParticipationRepository;
import com.example.Vox.Viridis.repository.RewardRepository;
import com.example.Vox.Viridis.repository.RewardTypeRepository;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ParticipationIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CampaignRepository campaigns;

    @Autowired
    private ParticipationRepository participations;

    @Autowired
    private UsersRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private RewardTypeRepository rewardTypes;

    @Autowired
    private RewardRepository rewards;

    @AfterEach
    void tearDown() {
        // clear the database after each test

        participations.deleteAll();
        rewards.deleteAll();
        campaigns.deleteAll();
        rewardTypes.deleteAll();

        users.deleteAll();
        roles.deleteAll();
    }

    @BeforeEach
    void createConsumerAccount() {
        users.deleteAll();
        roles.deleteAll();
        
        Role role = new Role(1l, "CONSUMER", null);
        role = roles.saveAndFlush(role);

        Users user = new Users();
        user.setUsername("customerParticipation");
        user.setEmail("customerParticipation@test.com");
        user.setFirstName("customer");
        user.setLastName("participation");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user.setRoles(role);
        user = users.saveAndFlush(user);
    }

    @BeforeEach
    void createRewardTypes() {
        rewardTypes.saveAllAndFlush(List.of(
                new RewardType(null, "Points", null),
                new RewardType(null, "Cards", null)));
    }

    private Users getCurrentUser() {
        return users.findByUsername("customerParticipation").get();
    }

    private Users createAdminAccount() {
        Role role = new Role(2l, "BUSINESS", null);
        role = roles.saveAndFlush(role);

        Users user = new Users();
        user.setUsername("adminParticipation");
        user.setEmail("adminParticipation@test.com");
        user.setFirstName("Admin2");
        user.setLastName("Participation");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user.setRoles(role);
        user = users.saveAndFlush(user);

        role.setUser(List.of(user));
        roles.saveAndFlush(role);

        return user;
    }

    private Campaign createCampaign(String campaignTitle, Users createdBy) {
        return campaigns.save(
                new Campaign(null,
                        campaignTitle, "campaign description",
                        LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                        "North", "SMU address", null, "Plastic", null, createdBy, LocalDateTime.now(), null));
    }
    private Campaign createCampaign(Users createdBy) {
        return createCampaign("Campaign title", createdBy);
    }

    private RewardType getRewardType(String rewardType) {
        return rewardTypes.findByRewardType(rewardType).get();
    }

    /*private String getJwtToken(String username) {
        ResponseEntity<String> tokenResponse = restTemplate.withBasicAuth(username, "goodpassword")
                .postForEntity(baseUrl + port + "/api/users/token", null, String.class);
        return tokenResponse.getBody();
    }*/

    private TestRestTemplate authenticatedRestTemplate(String username) {
        /*String jwtToken = getJwtToken(username);

        restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + jwtToken);
            return execution.execute(request, body);
        });
        return restTemplate;*/
        return restTemplate.withBasicAuth(username, "goodpassword");
    }
    private TestRestTemplate authenticatedConsumerRestTemplate() {
        return authenticatedRestTemplate("customerParticipation");
    }
    private TestRestTemplate authenticatedAdminRestTemplate() {
        return authenticatedRestTemplate("adminParticipation");
    }

    @Test
    public void getMyParticipation_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation");

        Users currentUser = getCurrentUser();
        Reward reward = new Reward(null, "reward name", createCampaign(createAdminAccount()), getRewardType("Points"),
                10, "tnc12345", null);
        reward = rewards.save(reward);

        Participation participation = new Participation(null, 0, LocalDateTime.now(), reward, currentUser);
        participation = participations.save(participation);

        ResponseEntity<PaginationDTO<Participation>> result = authenticatedConsumerRestTemplate().exchange(uri,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PaginationDTO<Participation>>() {
                });
        assertEquals(200, result.getStatusCode().value());
        PaginationDTO<Participation> participationsResult = result.getBody();
        assertNotNull(participationsResult);
        assertEquals(1, participationsResult.getTotalElements());
        assertEquals(1, participationsResult.getTotalNumPage());
        assertEquals(participation.getId(), participationsResult.getElements().get(0).getId());
    }

    @Test
    public void getMyParticipation_ReturnPage1_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation");
        final int CREATE_NUM_DATA = 25; // will create 25 sample data
        final int LIMIT = 20; // the limit that the backend will return

        Users currentUser = getCurrentUser();

        Users admin = createAdminAccount();
        List<Reward> rewardArr = new ArrayList<>();
        for (int i = 0; i < CREATE_NUM_DATA; i++) {
            Campaign campaign = createCampaign("Campaign title " + i, admin);
            Reward reward = new Reward(null, "reward name", campaign, getRewardType("Points"),
                    10, "tnc12345", null);
            rewardArr.add(reward);
        }
        rewardArr = rewards.saveAll(rewardArr);

        List<Participation> participationArr = new ArrayList<>();
        for (Reward reward : rewardArr) {
            Participation participation = new Participation(null, 0, LocalDateTime.now(), reward, currentUser);
            participationArr.add(participation);
        }
        participationArr = participations.saveAll(participationArr);

        ResponseEntity<PaginationDTO<Participation>> result = authenticatedConsumerRestTemplate().exchange(uri,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PaginationDTO<Participation>>() {
                });
        assertEquals(200, result.getStatusCode().value());
        PaginationDTO<Participation> participationsResult = result.getBody();
        assertNotNull(participationsResult);
        assertEquals(LIMIT, participationsResult.getElements().size());
        assertEquals(CREATE_NUM_DATA, participationsResult.getTotalElements());
        assertEquals(Math.ceil((double)CREATE_NUM_DATA / LIMIT), participationsResult.getTotalNumPage());
        for (int i = 0; i < LIMIT; i++) {
            Participation participation = participationArr.get(i);
            Participation actual = participationsResult.getElements().get(i);
            assertEquals(participation.getId(), actual.getId());
        }
    }

    @Test
    public void getMyParticipation_ReturnPage2_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation?pageNum=1"); // should return page 2
        final int CREATE_NUM_DATA = 25; // will create 25 sample data
        final int LIMIT = 20; // the limit that the backend will return

        Users currentUser = getCurrentUser();

        Users admin = createAdminAccount();
        List<Reward> rewardArr = new ArrayList<>();
        for (int i = 0; i < CREATE_NUM_DATA; i++) {
            Campaign campaign = createCampaign("Campaign title " + i, admin);
            Reward reward = new Reward(null, "reward name", campaign, getRewardType("Points"),
                    10, "tnc12345", null);
            rewardArr.add(reward);
        }
        rewardArr = rewards.saveAll(rewardArr);

        List<Participation> participationArr = new ArrayList<>();
        for (Reward reward : rewardArr) {
            Participation participation = new Participation(null, 0, LocalDateTime.now(), reward, currentUser);
            participationArr.add(participation);
        }
        participationArr = participations.saveAll(participationArr);

        ResponseEntity<PaginationDTO<Participation>> result = authenticatedConsumerRestTemplate().exchange(uri,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PaginationDTO<Participation>>() {
                });
        assertEquals(200, result.getStatusCode().value());
        PaginationDTO<Participation> participationsResult = result.getBody();
        assertNotNull(participationsResult);
        assertEquals(participationsResult.getTotalElements(), participationArr.size());
        assertEquals(Math.ceil((double)participationArr.size() / LIMIT), participationsResult.getTotalNumPage());
        assertEquals(participationArr.size() - LIMIT, participationsResult.getElements().size());
        for (int i = LIMIT; i < participationArr.size(); i++) {
            Participation participation = participationArr.get(i);
            Participation actual = participationsResult.getElements().get(i - LIMIT);
            assertEquals(participation.getId(), actual.getId());
        }
    }
    
    @Test
    public void getMyPoints_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation/myPoints");
        final int POINTS = 10;

        Users currentUser = getCurrentUser();
        currentUser.setPoints(POINTS);
        users.save(currentUser);
        
        ResponseEntity<Integer> result = authenticatedConsumerRestTemplate().exchange(uri,
                HttpMethod.GET, null,
                Integer.class);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(POINTS, result.getBody());
    }

    @Test
    public void createParticipation_New_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation");

        Reward reward = new Reward(null, "reward name", createCampaign(createAdminAccount()), getRewardType("Points"),
                10, "tnc12345", null);
        rewards.save(reward);

        ResponseEntity<Participation> result = authenticatedConsumerRestTemplate().exchange(uri + "/" + reward.getId(),
                HttpMethod.POST, null,
                Participation.class);
        assertEquals(201, result.getStatusCode().value());
        Participation participationResult = result.getBody();
        assertNotNull(participationResult);
        assertEquals(reward.getId(), participationResult.getReward().getId());
    }

    @Test
    public void createParticipation_Existing_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation");

        Reward reward = new Reward(null, "reward name", createCampaign(createAdminAccount()), getRewardType("Points"),
                10, "tnc12345", null);
        reward = rewards.save(reward);

        Participation participation = new Participation(null, 0, LocalDateTime.now(), reward, getCurrentUser());
        participations.save(participation);

        ResponseEntity<Participation> result = authenticatedConsumerRestTemplate().exchange(uri + "/" + reward.getId(),
                HttpMethod.POST, null,
                Participation.class);
        assertEquals(400, result.getStatusCode().value());
        Participation participationResult = result.getBody();
        assertNull(participationResult != null ? participationResult.getId() : participationResult);
    }

    @Test
    public void addPoints_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation/addPoints");

        Reward reward = new Reward(null, "reward name", createCampaign(createAdminAccount()), getRewardType("Points"),
                10, "tnc12345", null);
        rewards.save(reward);

        Participation participation = new Participation(null, 0, LocalDateTime.now(), reward, getCurrentUser());
        participation = participations.save(participation);

        ResponseEntity<Participation> result = authenticatedAdminRestTemplate().postForEntity(uri + "/" + participation.getId(),
                new ParticipationAddPointInputModel(1),
                Participation.class);
        assertEquals(200, result.getStatusCode().value());
        Participation participationResult = result.getBody();
        assertNotNull(participationResult);
        assertEquals(participation.getNoOfStamp(), participationResult.getNoOfStamp());
        assertEquals(10, getCurrentUser().getPoints()); // should add to currentUser.point instead
    }

    @Test
    public void addPoints_ParticipationNotExist_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/participation/addPoints");

        Reward reward = new Reward(null, "reward name", createCampaign(createAdminAccount()), getRewardType("Points"),
                10, "tnc12345", null);
        reward = rewards.save(reward);

        ResponseEntity<Participation> result = authenticatedAdminRestTemplate().postForEntity(uri + "/" + reward.getId(),
                new ParticipationAddPointInputModel(1),
                Participation.class);
        assertEquals(404, result.getStatusCode().value());
        Participation participationResult = result.getBody();
        assertNull(participationResult != null ? participationResult.getId() : participationResult);
    }
}
