package com.example.Vox.Viridis.service;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.ChatMessage;
import com.example.Vox.Viridis.model.MessageStatus;
import com.example.Vox.Viridis.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository repository;
    @Autowired private ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatId(senderId, recipientId, false);

        var messages =
                chatId.map(cId -> repository.findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            ChatMessage[] cm = repository.findBySenderIdAndRecipientId(senderId, recipientId);
            for(ChatMessage c : cm) {
            c.setStatus(MessageStatus.DELIVERED);
            repository.save(c);
            }
        }

        return messages;
    }

    public ChatMessage findById(String id) {
        return repository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return repository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("can't find message (" + id + ")"));
    }

    // public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
    //     CriteriaUpdate<ChatMessage> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ChatMessage.class);
    //     Root<ChatMessage> ChatMessageRoot = criteriaUpdate.from(ChatMessage.class);
    //     criteriaUpdate.set("status",status);
    //     criteriaUpdate.where();
        

    //               /*Query query = new Query(
    //             Criteria
    //                     .where("senderId").is(senderId)
    //                     .and("recipientId").is(recipientId));
    //     Update update = Update.update("status", status);
    //     mongoOperations.updateMulti(query, update, ChatMessage.class); */
    //}
}
