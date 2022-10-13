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
import org.junit.jupiter.api.BeforeEach;
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

import lombok.Data;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CampaignIntegrationTest {
    @LocalServerPort
	private int port;
    
    private final String baseUrl = "http://localhost:";

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
		campaigns.deleteAll();
	}

    @BeforeEach
    void createAdminAccount() {
        roles.deleteAll();
        users.deleteAll();

        Users user = new Users();
        user.setUsername("admin");
        user.setEmail("admin@test.com");
        user.setFirstName("Admin");
        user.setLastName("admin");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user = users.save(user);
        Role role = new Role(1l, "ROLE_BUSINESS", user);
        user.setRoles(List.of(role));
        roles.save(role);
    }

    private Users createSecondAccount() {
        Users user = new Users();
        user.setUsername("admin2");
        user.setEmail("admin2@test.com");
        user.setFirstName("Admin2");
        user.setLastName("admin2");
        user.setPassword(passwordEncoder.encode("goodpassword"));
        user = users.save(user);
        /*Role role = new Role(2l, "ROLE_BUSINESS", user);
        user.setRoles(List.of(role));
        roles.save(role);*/

        return user;
    }

    private TestRestTemplate authenticatedRestTemplate() {
        return restTemplate.withBasicAuth("admin", "goodpassword");
    }
    private Users getUser() {
        return users.findByUsername("admin").get();
    }

    private void modifyCampaignArr(List<Campaign> campaignArr) {
        campaignArr.forEach(c->{
            c.setCreatedBy(null);
        });
    }

    @Test
    public void getCampaigns_Sucess() throws Exception {
        Users user = getUser();
        URI uri = new URI(baseUrl + port + "/api/campaign");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(user);
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(user);
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		modifyCampaignArr(campaignArr);
        assertEquals(campaignArr, result.getBody());
    }

    @Test
    public void getCampaigns_MustContainCompanyNameField_Sucess() throws Exception {
        Users user = getUser();
        Users user2 = createSecondAccount();
        URI uri = new URI(baseUrl + port + "/api/campaign");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("user2");
        campaign.setCreatedBy(user2);
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().minusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("user1");
        campaign2.setCreatedBy(user);
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<CampaignCompanyName>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<CampaignCompanyName>>() {});
        assertEquals(200, result.getStatusCode().value());
		List<CampaignCompanyName> resultArr = result.getBody();
        assertNotNull(resultArr);
        resultArr.forEach(c -> {
            if (c.getTitle().equals("user1"))
                assertEquals(c.getCompanyName(), campaign2.companyName());
            else assertEquals(c.getCompanyName(), campaign.companyName());
        });
    }

    @Test
    public void getCampaigns_MustContainStatusField_Sucess() throws Exception {
        Users user = getUser();
        URI uri = new URI(baseUrl + port + "/api/campaign");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("Ongoing campaign");
        campaign.setCreatedBy(user);
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().minusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("Upcoming campaign");
        campaign2.setCreatedBy(user);
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<CampaignStatus>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<CampaignStatus>>() {});
        assertEquals(200, result.getStatusCode().value());
		List<CampaignStatus> resultArr = result.getBody();
        assertNotNull(resultArr);
        resultArr.forEach(c -> {
            if (c.getTitle().equals("Ongoing campaign"))
                assertEquals(c.getStatus(), 'O');
            else assertEquals(c.getStatus(), 'U');
        });
    }

    @Test
    public void getCampaigns_FilterTitle_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?filterByTitle=2");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(getUser());
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(getUser());
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		modifyCampaignArr(campaignArr);
		assertEquals(List.of(campaign2), result.getBody());
    }
    
    @Test
    public void getCampaigns_FilterCategory_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?category=clothing");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(getUser());
        campaign.setCategory("Clothing");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(getUser());
        campaign2.setCategory("Plastic");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		modifyCampaignArr(campaignArr);
		assertEquals(List.of(campaign), result.getBody());
    }
    
    @Test
    public void getCampaigns_FilterLocation_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/?location=North");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(getUser());
        campaign.setLocation("North");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(getUser());
        campaign2.setLocation("South");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		modifyCampaignArr(campaignArr);
		assertEquals(List.of(campaign), result.getBody());
    }
    
    @Test
    public void getCampaigns_OrderByNewest_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?isOrderByNewest=true");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(getUser());
        campaign.setLocation("North");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().minusMinutes(10).format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(getUser());
        campaign2.setLocation("South");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		modifyCampaignArr(campaignArr);
		assertEquals(List.of(campaign2, campaign), result.getBody());
    }
    
    @Test
    public void getCampaigns_OrderByOldest_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign?isOrderByNewest=false");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(getUser());
        campaign.setLocation("North");
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().minusMinutes(10).format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(getUser());
        campaign2.setLocation("South");
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<List<Campaign>> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Campaign>>() {});
        assertEquals(200, result.getStatusCode().value());
		modifyCampaignArr(campaignArr);
		assertEquals(List.of(campaign, campaign2), result.getBody());
    }

    @Test
    public void getCampaignById_Sucess() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setCreatedBy(getUser());
        campaign.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateFormat), dateFormat));
        campaign.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).plusDays(1).format(dateFormat), dateFormat));
        campaign.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(getUser());
        campaign2.setStartDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).format(dateFormat), dateFormat));
        campaign2.setEndDate(LocalDateTime.parse(LocalDateTime.now().plusMinutes(12).plusDays(1).format(dateFormat), dateFormat));
        campaign2.setCreatedOn(LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat));
        
        List<Campaign> campaignArr = List.of(campaign, campaign2);
        campaignArr = campaigns.saveAll(campaignArr);
        
        ResponseEntity<Campaign> result = authenticatedRestTemplate().exchange(uri + "/" + campaign2.getId(), HttpMethod.GET, null, Campaign.class);
        assertEquals(200, result.getStatusCode().value());
        modifyCampaignArr(campaignArr);
		assertEquals(campaign2, result.getBody());
    }

    @Test
    public void getCampaignById_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/158");
        
        ResponseEntity<Campaign> result = authenticatedRestTemplate().exchange(uri, HttpMethod.GET, null, Campaign.class);
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

        ResponseEntity<Campaign> result = authenticatedRestTemplate().postForEntity(uri, request, Campaign.class);
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

        ResponseEntity<Campaign> result = authenticatedRestTemplate().postForEntity(uri, request, Campaign.class);
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

        ResponseEntity<Campaign> result = authenticatedRestTemplate().postForEntity(uri, request, Campaign.class);
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

        ResponseEntity<Campaign> result = authenticatedRestTemplate().postForEntity(uri, request, Campaign.class);
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

        ResponseEntity<Campaign> result = authenticatedRestTemplate().postForEntity(uri, request, Campaign.class);
        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void addCampaign_DuplicateTitle_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaign.setCreatedBy(getUser());
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

        ResponseEntity<Campaign> result = authenticatedRestTemplate().postForEntity(uri, request, Campaign.class);
        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    public void updateCampaign_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");
        
        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaign.setCreatedBy(getUser());
        campaigns.save(campaign);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "New campaign 2");
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = authenticatedRestTemplate().exchange(uri + "/" + campaign.getId(), HttpMethod.PUT, request, Campaign.class);
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
        campaign.setCreatedBy(getUser());
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaigns.save(campaign);
        
        Campaign campaign2 = new Campaign();
        campaign2.setTitle("New campaign 2");
        campaign2.setCreatedBy(getUser());
        campaign2.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign2.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaigns.save(campaign2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", campaign.getTitle());
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = authenticatedRestTemplate().exchange(uri + "/" + campaign2.getId(), HttpMethod.PUT, request, Campaign.class);
        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    public void updateCampaign_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String startDate = LocalDateTime.now().plusMinutes(20).format(dateformat);
        String endDate = LocalDateTime.now().plusMinutes(20).plusDays(1).format(dateformat);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("title", "Campaign doesn't exist");
        map.add("startDate", startDate);
        map.add("endDate", endDate);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<Campaign> result = authenticatedRestTemplate().exchange(uri, HttpMethod.PUT, request, Campaign.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteCampaign_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");

        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaign.setCreatedBy(getUser());
        campaigns.save(campaign);

        ResponseEntity<Void> result = authenticatedRestTemplate().exchange(uri + "/" + campaign.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(200, result.getStatusCode().value());
        assertTrue(campaigns.findById(campaign.getId()).isEmpty());
    }

    @Test
    public void deleteCampaign_NotFound_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign/123");

        ResponseEntity<Void> result = authenticatedRestTemplate().exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteCampaign_Unauthorised_Fail() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/campaign");

        Campaign campaign = new Campaign();
        campaign.setTitle("New campaign");
        campaign.setStartDate(LocalDateTime.now().plusMinutes(2));
        campaign.setEndDate(LocalDateTime.now().plusMinutes(2).plusDays(1));
        campaign.setCreatedBy(createSecondAccount());
        campaigns.save(campaign);

        ResponseEntity<Void> result = authenticatedRestTemplate().exchange(uri + "/" + campaign.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(403, result.getStatusCode().value());
    }
}

// For getCampaigns_MustContainStatusField_Sucess() test
// To check it returns 'status' field
@Data
class CampaignStatus { 
    private String title; 
    private char status; 
}

// For getCampaigns_MustContainCompanyNameField_Sucess() test
// To check it returns 'companyName' field
@Data
class CampaignCompanyName { 
    private String title; 
    private String companyName; 
}