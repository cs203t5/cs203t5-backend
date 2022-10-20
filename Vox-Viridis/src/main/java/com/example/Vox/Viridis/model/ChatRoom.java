package com.example.Vox.Viridis.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class ChatRoom {
    @Id @GeneratedValue 
    private String id;
    private String chatId;
    private String senderName;
    private String recipientName;

    //added
    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages;

    //added
    @ManyToMany
    @JoinTable(name = "user_chatroom",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "chatroom_id"))
    private Set<Users> users = new HashSet<>();
}