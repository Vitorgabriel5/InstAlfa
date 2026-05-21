package AlfaInsta.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID postId;
    private UUID userId;

    @Column(length = 1000)
    private String content;

    private LocalDateTime createdAt;
}