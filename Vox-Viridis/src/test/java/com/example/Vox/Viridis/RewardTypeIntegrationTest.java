package com.example.Vox.Viridis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.example.Vox.Viridis.model.RewardType;
import com.example.Vox.Viridis.repository.RewardTypeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RewardTypeIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private RewardTypeRepository rewardTypes;

        @AfterEach
        void tearDown() {
                // clear the database after each test
                rewardTypes.deleteAll();
        }

        @Test
        public void getRewardType_Sucess() throws Exception {
                URI uri = new URI(baseUrl + port + "/api/rewardType");

                List<RewardType> rewardTypeArr = List.of(
                    new RewardType(null, "Cards", null),
                    new RewardType(null, "Points", null)
                );
                rewardTypeArr = rewardTypes.saveAll(rewardTypeArr);

                ResponseEntity<List<RewardType>> result = restTemplate.exchange(uri,
                                HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<RewardType>>() {});
                assertEquals(200, result.getStatusCode().value());
                assertEquals(rewardTypeArr, result.getBody());
        }
}
