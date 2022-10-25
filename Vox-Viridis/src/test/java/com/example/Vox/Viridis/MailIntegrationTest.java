package com.example.Vox.Viridis;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.Vox.Viridis.repository.RoleRepository;
import com.example.Vox.Viridis.repository.UsersRepository;
import com.example.Vox.Viridis.service.MailService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MailIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private MailService mailService;

    @Test
    public void sendEmail_success() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("voxviridis5@gmail.com");
        simpleMailMessage.setTo("voxviridis5@gmail.com");
        simpleMailMessage.setSubject("test subject");
        simpleMailMessage.setText("test text");

        boolean isSent = mailService.sendMailMessage(simpleMailMessage);
        assertTrue(isSent);
    }
}
