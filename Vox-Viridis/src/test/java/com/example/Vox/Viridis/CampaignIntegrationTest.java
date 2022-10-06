package com.example.Vox.Viridis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.CampaignRepository;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CampaignIntegrationTest {
    @LocalServerPort
	private int port;
    
    private final String baseUrl = "http://localhost:";
    private final String USERNAME = "admin";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
	private CampaignRepository campaigns;

    @Autowired
	private UsersRepository users;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
	private RoleRepository roles;

    @AfterEach
	void tearDown(){
		// clear the database after each test
        roles.deleteAll();
        users.deleteAll();
		campaigns.deleteAll();
	}

    private TestRestTemplate createAdminAccount() {
        Users admin = new Users();
        admin.setUsername(USERNAME);
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");
        admin.setLastName("admin");
        admin.setPassword(passwordEncoder.encode("goodpassword"));
        Role role = new Role(1l, "ROLE_BUSINESS", admin);
        admin.setRoles(List.of(role));
        admin = users.save(admin);
        roles.save(role);

        return restTemplate.withBasicAuth("admin", "goodpassword");
    }

    @Test
    public void getCampaigns_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = createAdminAccount().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		assertEquals(campaignArr, result.getBody());
    }

    @Test
    public void getCampaigns_FilterTitle_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?filterByTitle=2");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = createAdminAccount().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		assertEquals(List.of(campaign2), result.getBody());
    }
    
    @Test
    public void getCampaigns_FilterCategory_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?category=clothing");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setCategory("Clothing");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setCategory("Plastic");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = createAdminAccount().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		assertEquals(List.of(campaign), result.getBody());
    }
    
    @Test
    public void getCampaigns_FilterLocation_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/?location=North");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setLocation("North");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setLocation("South");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = createAdminAccount().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		assertEquals(List.of(campaign), result.getBody());
    }
    
    @Test
    public void getCampaigns_OrderByNewest_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?isOrderByNewest=true");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setLocation("North");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().minusMinutes(10).format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setLocation("South");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = createAdminAccount().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		assertEquals(List.of(campaign2, campaign), result.getBody());
    }
    
    @Test
    public void getCampaigns_OrderByOldest_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?isOrderByNewest=false");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setLocation("North");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().minusMinutes(10).format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setLocation("South");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = createAdminAccount().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		assertEquals(List.of(campaign, campaign2), result.getBody());
    }

    @Test
    public void getCampaignById_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<Campaign> result = createAdminAccount().exchange(uri + "/" + campaign2.getId(), HttpMethod.GET, null, Campaign.class);
        assertEquals(200, result.getStatusCode().value());
		assertEquals(campaign2, result.getBody());
    }

    @Test
    public void getCampaignById_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/158");
        
        ResponseEntity<Campaign> result = createAdminAccount().exchange(uri, HttpMethod.GET, null, Campaign.class);
        assertEquals(404, result.getStatusCode().value());
		assertNull(result.getBody().getTitle());
    }

    @Test
    public void addCampaign_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "New campaign");
        map.add("startDate", startDate);
        map.add("endDate", endDate);
        map.add("location", "north");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().postForEntity(uri, request, Campaign.class);
        assertEquals(201, result.getStatusCode().value());
        Campaign campaign = result.getBody();
        assertNotNull(campaign);
		assertEquals("New campaign", campaign.getTitle());
		assertEquals(startDate, campaign.getStartDate().format(dateformat));
		assertEquals(endDate, campaign.getEndDate().format(dateformat));
        assertEquals("north", campaign.getLocation());
    }

    @Test
    public void addCampaign_InvalidLocation_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "New campaign");
        map.add("startDate", startDate);
        map.add("endDate", endDate);
        map.add("location", "Abc");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().postForEntity(uri, request, Campaign.class);
        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void addCampaign_WithoutTime_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat) + " 00:00";
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat) + " 00:00";
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "New campaign");
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        ResponseEntity<Campaign> result = createAdminAccount().postForEntity(uri, request, Campaign.class);
        assertEquals(201, result.getStatusCode().value());
        Campaign campaign = result.getBody();
        assertNotNull(campaign);
		assertEquals("New campaign", campaign.getTitle());
		assertEquals(startDate, campaign.getStartDate().format(dateformat));
		assertEquals(endDate, campaign.getEndDate().format(dateformat));
    }

    @Test
    public void addCampaign_InvalidTime_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().minusMinutes(20).format(dateformat) + " 00:00";
        String endDate = LocalDateTime.now().minusMinutes(20).plusDays(1).format(dateformat) + " 00:00";
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "New campaign");
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().postForEntity(uri, request, Campaign.class);
        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void addCampaign_InconsistentDate_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "New campaign");
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().postForEntity(uri, request, Campaign.class);
        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void addCampaign_DuplicateTitle_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaign.setCreatedBy(USERNAME);
        campaigns.save(campaign);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", campaign.getTitle());
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().postForEntity(uri, request, Campaign.class);
        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    public void updateCampaign_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaign.setCreatedBy(USERNAME);
        campaigns.save(campaign);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "New campaign 2");
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().exchange(uri + "/" + campaign.getId(), HttpMethod.PUT, request, Campaign.class);
        assertEquals(200, result.getStatusCode().value());
        Campaign results = result.getBody();
        assertNotNull(results);
        assertEquals("New campaign 2", results.getTitle());
        assertEquals(startDate, results.getStartDate().format(dateformat));
        assertEquals(endDate, results.getEndDate().format(dateformat));
    }

    @Test
    public void updateCampaign_DuplicateTitle_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(USERNAME);
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaigns.save(campaign);
        
        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(USERNAME);
        campaign2.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign2.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaigns.save(campaign2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", campaign.getTitle());
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().exchange(uri + "/" + campaign2.getId(), HttpMethod.PUT, request, Campaign.class);
        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    public void updateCampaign_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "Campaign doesn't exist");
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = createAdminAccount().exchange(uri, HttpMethod.PUT, request, Campaign.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteCampaign_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");

        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaign.setCreatedBy(USERNAME);
        campaigns.save(campaign);

        ResponseEntity<Void> result = createAdminAccount().exchange(uri + "/" + campaign.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(200, result.getStatusCode().value());
        assertTrue(campaigns.findById(campaign.getId()).isEmpty());
    }

    @Test
    public void deleteCampaign_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/123");

        ResponseEntity<Void> result = createAdminAccount().exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(404, result.getStatusCode().value());
    }
}
