package AlfaInsta.demo.repository;

import AlfaInsta.demo.model.Repost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepostRepository extends JpaRepository<Repost, UUID> {
    boolean existsByUserIdAndOriginalPostId(UUID userId, UUID originalPostId);
    long countByOriginalPostId(UUID originalPostId);
    Optional<Repost> findByUserIdAndOriginalPostId(UUID userId, UUID originalPostId);
}