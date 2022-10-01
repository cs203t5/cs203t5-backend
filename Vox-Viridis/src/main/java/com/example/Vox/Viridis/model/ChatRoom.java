package com.example.Vox.Viridis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class ChatRoom {
    @Id @GeneratedValue 
    private String id;
    private String chatId;
    private String senderId;
    private String recipientId;
}
