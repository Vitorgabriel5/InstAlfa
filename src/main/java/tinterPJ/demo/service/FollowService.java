package tinterPJ.demo.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.UserResponseDTO;
import tinterPJ.demo.model.Follow;
import tinterPJ.demo.repository.FollowRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;
    private final NotificationService notificationService; // ← adicionar

    @Transactional
    public void follow(UUID followerId, UUID followingId) {

        if (followerId.equals(followingId)) {
            throw new RuntimeException("Você não pode seguir a si mesmo");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("Já está seguindo");
        }

        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();

        followRepository.save(follow);

        // ← adicionar isso
        notificationService.create(
                followingId,
                followerId,
                "follow",
                "começou a seguir você."
        );
    }

    @Transactional
    public void unfollow(UUID followerId, UUID followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("Você não segue esse usuário");
        }
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }


    public long countFollowers(UUID userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long countFollowing(UUID userId) {
        return followRepository.countByFollowerId(userId);
    }

    public List<UserResponseDTO> getFollowers(UUID userId, UUID currentUserId) {
        return followRepository.findByFollowingId(userId)
                .stream()
                .map(f -> userService.findById(f.getFollowerId()))
                .map(user -> userService.toResponse(user, currentUserId))
                .toList();
    }

    public List<UserResponseDTO> getFollowing(UUID userId, UUID currentUserId) {
        return followRepository.findByFollowerId(userId)
                .stream()
                .map(f -> userService.findById(f.getFollowingId()))
                .map(user -> userService.toResponse(user, currentUserId))
                .toList();
    }
}