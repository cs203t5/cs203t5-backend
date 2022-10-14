package com.example.Vox.Viridis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Vox.Viridis.model.ChatRoom;
import java.util.Optional;

public interface ChatRoomRespository extends JpaRepository<ChatRoom, String>{
    Optional<ChatRoom> findBySenderNameAndRecipientName(String senderName, String recipientName);
}
