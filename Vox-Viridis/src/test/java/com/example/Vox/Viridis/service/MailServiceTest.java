package com.example.Vox.Viridis.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;

@SpringBootTest
public class MailServiceTest {

    @Mock
    private MailService mailService;

    @Test
    void sendEmail_validFormat_returnTrue() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("${VOX_VIRIDIS_EMAIL}");
        simpleMailMessage.setTo("${VOX_VIRIDIS_EMAIL}");
        simpleMailMessage.setSubject("test subject");
        simpleMailMessage.setText("test text");

        when(mailService.sendMailMessage(simpleMailMessage)).thenReturn(true);
        boolean isSent = mailService.sendMailMessage(simpleMailMessage);
        assertTrue(isSent);
        verify(mailService).sendMailMessage(simpleMailMessage);
    }
}
