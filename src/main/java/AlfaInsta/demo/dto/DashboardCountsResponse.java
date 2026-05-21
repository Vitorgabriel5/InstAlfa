package AlfaInsta.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardCountsResponse {
    private long unreadNotifications;
    private long unreadMessages;
}
