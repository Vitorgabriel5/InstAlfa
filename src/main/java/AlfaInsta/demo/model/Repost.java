package AlfaInsta.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reposts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "original_post_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repost {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "original_post_id", nullable = false)
    private UUID originalPostId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}