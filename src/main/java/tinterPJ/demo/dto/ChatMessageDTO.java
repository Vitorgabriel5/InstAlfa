package tinterPJ.demo.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private String type;
    private boolean seen;
    private LocalDateTime createdAt;
}