package com.example.Vox.Viridis.repository;
import com.example.Vox.Viridis.model.ChatMessage;
import com.example.Vox.Viridis.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String>{
    long countBySenderIdAndRecipientIdAndStatus(
            String senderId, String recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);

    // @Modifying
    // @Query("update ChatMessage u set u.MessageStatus = ?1 where u.senderId = ?2 and u.recipientId=?3")
    // void updateStatus(MessageStatus status,String senderId, String recipientId);

    ChatMessage[] findBySenderIdAndRecipientId(String SenderId, String recipientId);

}
