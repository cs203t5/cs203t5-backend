package com.example.Vox.Viridis.service;

import com.example.Vox.Viridis.model.ChatRoom;
import com.example.Vox.Viridis.repository.ChatRoomRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRespository chatRoomRepository;

    public Optional<String> getChatId(
            String senderName, String recipientName, boolean createIfNotExist) {

        return chatRoomRepository
                .findBySenderNameAndRecipientName(senderName, recipientName)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (!createIfNotExist) {
                        return Optional.empty();
                    }
                    var chatId = String.format("%s_%s", senderName, recipientName);

                    ChatRoom senderRecipient = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderName(senderName)
                            .recipientName(recipientName)
                            .build();

                    ChatRoom recipientSender = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderName(recipientName)
                            .recipientName(senderName)
                            .build();
                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }
}
