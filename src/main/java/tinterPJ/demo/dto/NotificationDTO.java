package tinterPJ.demo.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificationDTO {
    private UUID id;
    private UUID fromUserId;
    private String fromUsername;
    private String fromAvatar;
    private String type;
    private String text;
    private boolean read;
    private LocalDateTime createdAt;
}