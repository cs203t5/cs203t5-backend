package com.example.Vox.Viridis.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Vox.Viridis.model.Contact;
import com.example.Vox.Viridis.service.MailService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("email")
@RequiredArgsConstructor
public class EmailController {
    private final MailService mailService;
    @Value("${VOX_VIRIDIS_EMAIL}")
    private String EMAIL;

    @PostMapping
    public ResponseEntity<Object> sendEmail(@Valid @RequestBody Contact email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(EMAIL);
        simpleMailMessage.setTo(EMAIL);
        simpleMailMessage
                .setSubject("Question from " + email.getFirstName() + " " + email.getLastName());
        simpleMailMessage.setText("Email: " + email.getEmail() + "\n" + "First Name: "
                + email.getFirstName() + "\n" + "Last name: " + email.getLastName() + "\n"
                + "Question: " + email.getQuestion() + "\n");
        boolean isSent = mailService.sendMailMessage(simpleMailMessage);
        if (isSent) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
