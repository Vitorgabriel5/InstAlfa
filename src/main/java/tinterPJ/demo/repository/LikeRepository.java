package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tinterPJ.demo.model.Like;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByUserIdAndPostId(UUID userId, UUID postId);

    long countByPostId(UUID postId);

    void deleteByUserIdAndPostId(UUID userId, UUID postId);
}