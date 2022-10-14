package com.example.Vox.Viridis.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ChatNotification {
    private String id;
    private Long senderId;
    private String senderName;
}
