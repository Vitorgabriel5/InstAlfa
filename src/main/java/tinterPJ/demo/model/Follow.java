package tinterPJ.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId; // quem segue

    @Column(name = "following_id", nullable = false)
    private UUID followingId; // quem é seguido
}
