package AlfaInsta.demo.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private String username;
    private String profilePicture;
    private String content;
    private LocalDateTime createdAt;
}