package com.example.Vox.Viridis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendMailMessage(final SimpleMailMessage simpleMailMessage) {
        try{
            this.javaMailSender.send(simpleMailMessage);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
