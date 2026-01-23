package tinterPJ.demo.messaging.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchNotificationEvent implements Serializable {

    private Long matchId;
    private Long user1Id;
    private String  user1Nome;
    private Long user2Id;
    private String  user2Nome;
    private LocalDateTime dataMatch;
}
