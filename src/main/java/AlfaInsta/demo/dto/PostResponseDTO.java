package AlfaInsta.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PostResponseDTO {

    private UUID id;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    private UUID userId;
    private String username;
    private String profilePicture;

    private long likes;
    private boolean liked;
    private long comments;
    private long reposts;
    private boolean reposted;

}