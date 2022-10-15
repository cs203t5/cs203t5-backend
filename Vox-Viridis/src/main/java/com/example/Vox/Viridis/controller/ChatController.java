package com.example.Vox.Viridis.controller;

import com.example.Vox.Viridis.model.ChatMessage;
import com.example.Vox.Viridis.model.ChatNotification;
import com.example.Vox.Viridis.service.ChatMessageService;
import com.example.Vox.Viridis.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {
     @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatId(chatMessage.getSenderName(), chatMessage.getRecipientName(), true);
        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientName(),"/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName()));
    }

    @GetMapping("/messages/{senderName}/{recipientName}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable String senderName,
            @PathVariable String recipientName) {

        return ResponseEntity
                .ok(chatMessageService.countNewMessages(senderName, recipientName));
    }

    @GetMapping("/messages/{senderName}/{recipientName}")
    public ResponseEntity<?> findChatMessages ( @PathVariable String senderName,
                                                @PathVariable String recipientName) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderName, recipientName));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable String id) {
        return ResponseEntity
                .ok(chatMessageService.findById(id));
    }
}
