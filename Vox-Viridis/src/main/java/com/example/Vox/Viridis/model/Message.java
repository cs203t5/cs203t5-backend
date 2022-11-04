package com.example.Vox.Viridis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Data
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue
    private long id;
    private String senderName;
    private String receiverName;
    private String message;
    private String date;
    private Status status;
}
