package AlfaInsta.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import AlfaInsta.demo.model.Follow;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowingId(UUID followingId);

    long countByFollowerId(UUID followerId);

    List<Follow> findByFollowingId(UUID followingId);

    List<Follow> findByFollowerId(UUID followerId);
}