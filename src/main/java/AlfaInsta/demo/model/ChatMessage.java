package AlfaInsta.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID senderId;
    private UUID receiverId;

    @Column(length = 2000)
    private String content;

    private String type; // "text" ou "image"

    private boolean seen = false;

    private LocalDateTime createdAt;
}