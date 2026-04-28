package tinterPJ.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.PostResponseDTO;
import tinterPJ.demo.model.Like;
import tinterPJ.demo.model.Post;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.FollowRepository;
import tinterPJ.demo.repository.LikeRepository;
import tinterPJ.demo.repository.PostRepository;
import tinterPJ.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public Post create(String content, String imageUrl, UUID userId) {
        Post post = new Post();
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setUserId(userId);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public List<PostResponseDTO> getFeed(UUID userId) {

        List<UUID> followingIds = followRepository.findByFollowingId(userId)
                .stream()
                .map(f -> f.getFollowingId())
                .toList();

        followingIds.add(userId);

        List<Post> posts = postRepository
                .findByUserIdInOrderByCreatedAtDesc(followingIds);
        return posts.stream().map(post -> {

            User user = userRepository.findById(post.getUserId()).orElseThrow();

            long likesCount = likeRepository.countByPostId(post.getId());

            boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());

            return new PostResponseDTO(
                    post.getId(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getCreatedAt(),
                    user.getId(),
                    user.getUsername(),
                    user.getProfilePicture(),
                    likesCount,
                    liked
            );

        }).toList();
    }

    public void toggleLike(UUID userId, UUID postId) {
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            likeRepository.deleteByUserIdAndPostId(userId, postId);
        }else  {
            Like like = new Like();
            like.setUserId(userId);
            like.setPostId(postId);

            likeRepository.save(like);
        }
    }
}
