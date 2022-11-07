package com.example.Vox.Viridis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.time.LocalDateTime;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.RewardInputModel;
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
public class RewardIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CampaignRepository campaigns;

    @Autowired
    private RewardRepository rewards;

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
    void createAdminAccount() {
        users.deleteAll();
        roles.deleteAll();

        Role businessRole = new Role(1l, "BUSINESS", null);
        Role consumerRole = new Role(2l, "CONSUMER", null);
        businessRole = roles.save(businessRole);
        consumerRole = roles.save(consumerRole);

        Users user = new Users();
        user.setUsername("admin");
        user.setEmail("admin@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user.setRoles(businessRole);
        user = users.save(user);
    }

    private Users createAnotherAdminAccount() {
        Users user = new Users();
        user.setUsername("admin2");
        user.setEmail("admin2@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user.setRoles(roles.findByName("BUSINESS"));
        return users.save(user);
    }

    @BeforeEach
    void createRewardTypes() {
        rewardTypes.saveAll(List.of(
                new RewardType(null, "Points", null),
                new RewardType(null, "Cards", null)));
    }

    private Users createConsumerAccount() {
        Users user = new Users();
        user.setUsername("customer");
        user.setEmail("customer@test.com");
        user.setFirstName("customer");
        user.setLastName("consumer");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user.setRoles(roles.findByName("CONSUMER"));
        user = users.save(user);

        return user;
    }
    private Users createAnotherConsumerAccount() {
        Users user = new Users();
        user.setUsername("customer2");
        user.setEmail("customer2@test.com");
        user.setFirstName("customer");
        user.setLastName("consumer");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user.setRoles(roles.findByName("CONSUMER"));
        user = users.save(user);

        return user;
    }

    private String getJwtToken(String username) {
        ResponseEntity<String> tokenResponse = restTemplate.withBasicAuth(username, "goodpassword")
                .postForEntity(baseUrl + port + "/api/users/token", null, String.class);
        return tokenResponse.getBody();
    }

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
        return authenticatedRestTemplate("customer");
    }

    private TestRestTemplate authenticatedAdminRestTemplate() {
        return authenticatedRestTemplate("admin");
    }

    private Users getAdminUser() {
        return users.findByUsername("admin").get();
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

    @Test
    public void getMyRewards_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward/myReward");

        Reward reward1 = new Reward(null, "reward name", createCampaign(getAdminUser()), getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);
        Participation participation1 = new Participation(null, 0, LocalDateTime.now(), reward1, createConsumerAccount());
        participations.save(participation1);

        Reward reward2 = new Reward(null, "reward name", createCampaign("campaign 2", getAdminUser()), getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward2);
        Participation participation2 = new Participation(null, 0, LocalDateTime.now(), reward2, createAnotherConsumerAccount());
        participations.save(participation2);

        ResponseEntity<PaginationDTO<Reward>> result = authenticatedConsumerRestTemplate().exchange(uri,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PaginationDTO<Reward>>() {
                });
        assertEquals(200, result.getStatusCode().value());
        PaginationDTO<Reward> rewardResult = result.getBody();
        assertNotNull(rewardResult);
        assertEquals(1, rewardResult.getElements().size());
        assertEquals(reward1.getId(), rewardResult.getElements().get(0).getId());
    }

    @Test
    public void getRewards_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward");

        Reward reward1 = new Reward(null, "reward name", createCampaign(getAdminUser()), getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);

        ResponseEntity<PaginationDTO<Reward>> result = restTemplate.exchange(uri,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PaginationDTO<Reward>>() {
                });
        assertEquals(200, result.getStatusCode().value());
        PaginationDTO<Reward> rewardResult = result.getBody();
        assertNotNull(rewardResult);
        assertEquals(1, rewardResult.getElements().size());
        assertEquals(reward1.getId(), rewardResult.getElements().get(0).getId());
    }

    @Test
    public void getRewardsByCampaignId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward/byCampaign");

        Campaign campaign1 = createCampaign(getAdminUser());
        Reward reward1 = new Reward(null, "reward name", campaign1, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);

        Campaign campaign2 = createCampaign("Another campaign", getAdminUser());
        Reward reward2 = new Reward(null, "reward name", campaign2, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward2);

        ResponseEntity<Reward> result = restTemplate.exchange(uri + "/" + campaign1.getId(),
                HttpMethod.GET, null,
                Reward.class);
        assertEquals(200, result.getStatusCode().value());
        Reward rewardResult = result.getBody();
        assertNotNull(rewardResult);
        assertEquals(reward1.getId(), rewardResult.getId());
    }

    @Test
    public void getRewardsById_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward");

        Campaign campaign1 = createCampaign(getAdminUser());
        Reward reward1 = new Reward(null, "reward name", campaign1, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);

        Campaign campaign2 = createCampaign("Another campaign", getAdminUser());
        Reward reward2 = new Reward(null, "reward name", campaign2, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward2);

        ResponseEntity<Reward> result = restTemplate.exchange(uri + "/" + reward1.getId(),
                HttpMethod.GET, null,
                Reward.class);
        assertEquals(200, result.getStatusCode().value());
        Reward rewardResult = result.getBody();
        assertNotNull(rewardResult);
        assertEquals(reward1.getId(), rewardResult.getId());
    }

    @Test
    public void createReward_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward");

        Campaign campaign1 = createCampaign(getAdminUser());
        RewardInputModel reward1 = new RewardInputModel("Points", "reward", 10, "terms and conditions");
        
        ResponseEntity<Reward> result = authenticatedAdminRestTemplate().postForEntity(uri + "/" + campaign1.getId(),
                reward1, Reward.class);
        assertEquals(201, result.getStatusCode().value());
        Reward rewardResult = result.getBody();
        assertNotNull(rewardResult);

        assertEquals(reward1.getRewardName(), rewardResult.getRewardName());
    }

    @Test
    public void createReward_CamignIdNotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward/999"); // campaign id not found

        RewardInputModel reward1 = new RewardInputModel("Points", "reward", 10, "terms and conditions");
        
        ResponseEntity<Reward> result = authenticatedAdminRestTemplate().postForEntity(uri,
                reward1, Reward.class);
        assertEquals(404, result.getStatusCode().value());
        Reward rewardResult = result.getBody();
        assertNull(rewardResult != null ? rewardResult.getRewardName() : rewardResult);
    }

    @Test
    public void updateReward_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward");

        Campaign campaign1 = createCampaign(getAdminUser());
        Reward reward1 = new Reward(null, "reward name", campaign1, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);
        RewardInputModel rewardInput1 = new RewardInputModel("Points", "new reward", 10, "terms and conditions");
        
        ResponseEntity<Reward> result = authenticatedAdminRestTemplate().exchange(uri + "/" + reward1.getId(),
            HttpMethod.PUT, new HttpEntity<>(rewardInput1), Reward.class);
        assertEquals(200, result.getStatusCode().value());
        Reward rewardResult = result.getBody();
        assertNotNull(rewardResult);
        assertEquals(rewardInput1.getRewardName(), rewardResult.getRewardName());
    }

    @Test
    public void updateReward_NotOwner_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward");

        Campaign campaign1 = createCampaign(createAnotherAdminAccount()); // created by another user
        Reward reward1 = new Reward(null, "reward name", campaign1, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);
        RewardInputModel rewardInput1 = new RewardInputModel("Points", "new reward", 10, "terms and conditions");
        
        ResponseEntity<Reward> result = authenticatedAdminRestTemplate().exchange(uri + "/" + reward1.getId(),
            HttpMethod.PUT, new HttpEntity<>(rewardInput1), Reward.class);
        assertEquals(403, result.getStatusCode().value());
        Reward rewardResult = result.getBody();
        assertNull(rewardResult != null ? rewardResult.getRewardName() : rewardResult);
    }

    @Test
    public void updateReward_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward/999");

        RewardInputModel rewardInput1 = new RewardInputModel("Points", "new reward", 10, "terms and conditions");
        
        ResponseEntity<Reward> result = authenticatedAdminRestTemplate().exchange(uri,
            HttpMethod.PUT, new HttpEntity<>(rewardInput1), Reward.class);
        assertEquals(404, result.getStatusCode().value());
        Reward rewardResult = result.getBody();
        assertNull(rewardResult != null ? rewardResult.getRewardName() : rewardResult);
    }

    @Test
    public void deleteReward_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward/999");
        
        ResponseEntity<Void> result = authenticatedAdminRestTemplate().exchange(uri,
            HttpMethod.DELETE, null, Void.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteReward_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward");

        Campaign campaign1 = createCampaign(getAdminUser()); // created by another user
        Reward reward1 = new Reward(null, "reward name", campaign1, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);
        
        ResponseEntity<Void> result = authenticatedAdminRestTemplate().exchange(uri + "/" + reward1.getId(),
            HttpMethod.DELETE, null, Void.class);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void deleteReward_NotOwner_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/reward");

        Campaign campaign1 = createCampaign(createAnotherAdminAccount()); // created by another user
        Reward reward1 = new Reward(null, "reward name", campaign1, getRewardType("Points"), 10, "terms and conditions", null);
        rewards.save(reward1);
        
        ResponseEntity<Void> result = authenticatedAdminRestTemplate().exchange(uri + "/" + reward1.getId(),
            HttpMethod.DELETE, null, Void.class);
        assertEquals(403, result.getStatusCode().value());
    }
}
