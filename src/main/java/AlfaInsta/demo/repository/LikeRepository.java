package AlfaInsta.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import AlfaInsta.demo.model.Like;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByUserIdAndPostId(UUID userId, UUID postId);

    long countByPostId(UUID postId);

    @Transactional
    void deleteByUserIdAndPostId(UUID userId, UUID postId);
}