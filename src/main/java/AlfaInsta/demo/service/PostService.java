package AlfaInsta.demo.service;

import AlfaInsta.demo.dto.CommentResponseDTO;
import AlfaInsta.demo.model.*;
import AlfaInsta.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import AlfaInsta.demo.dto.PostResponseDTO;

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
    private final NotificationService notificationService;
    private final CommentRepository commentRepository;
    private final RepostRepository repostRepository;

    public CommentResponseDTO addComment(UUID postId, UUID userId, String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        User user = userRepository.findById(userId).orElseThrow();

        Post post = postRepository.findById(postId).orElseThrow();
        if (!post.getUserId().equals(userId)) {
            notificationService.create(post.getUserId(), userId, "mention", "comentou no seu post.");
        }

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .username(user.getUsername())
                .profilePicture(user.getProfilePicture())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public List<CommentResponseDTO> getComments(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(c -> {
                    User user = userRepository.findById(c.getUserId()).orElse(null);
                    return CommentResponseDTO.builder()
                            .id(c.getId())
                            .postId(c.getPostId())
                            .userId(c.getUserId())
                            .username(user != null ? user.getUsername() : "unknown")
                            .profilePicture(user != null ? user.getProfilePicture() : null)
                            .content(c.getContent())
                            .createdAt(c.getCreatedAt())
                            .build();
                }).toList();
    }

    public Post create(String content, String imageUrl, UUID userId) {
        Post post = new Post();
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setUserId(userId);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<PostResponseDTO> getFeed(UUID userId) {
        List<UUID> followingIds = new ArrayList<>(
                followRepository.findByFollowerId(userId)
                        .stream()
                        .map(f -> f.getFollowingId())
                        .toList()
        );
        followingIds.add(userId);

        List<Post> posts = postRepository.findByUserIdInOrderByCreatedAtDesc(followingIds);

        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElseThrow();

            long likesCount = likeRepository.countByPostId(post.getId());
            boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
            long commentsCount = commentRepository.countByPostId(post.getId());
            long repostsCount = repostRepository.countByOriginalPostId(post.getId());
            boolean reposted = repostRepository.existsByUserIdAndOriginalPostId(userId, post.getId());

            return new PostResponseDTO(
                    post.getId(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getCreatedAt(),
                    user.getId(),
                    user.getUsername(),
                    user.getProfilePicture(),
                    likesCount,
                    liked,
                    commentsCount,
                    repostsCount,
                    reposted
            );
        }).toList();
    }

    public PostResponseDTO getPostById(UUID postId, UUID currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        long likesCount = likeRepository.countByPostId(post.getId());
        boolean liked = likeRepository.existsByUserIdAndPostId(currentUserId, post.getId());
        long commentsCount = commentRepository.countByPostId(post.getId());
        long repostsCount = repostRepository.countByOriginalPostId(post.getId());
        boolean reposted = repostRepository.existsByUserIdAndOriginalPostId(currentUserId, post.getId());

        return new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt(),
                user.getId(),
                user.getUsername(),
                user.getProfilePicture(),
                likesCount,
                liked,
                commentsCount,
                repostsCount,
                reposted
        );
    }

    public List<PostResponseDTO> getPostsByUser(UUID userId) {
        return postRepository.findByUserIdInOrderByCreatedAtDesc(List.of(userId))
                .stream()
                .map(post -> {
                    User user = userRepository.findById(post.getUserId()).orElseThrow();
                    long likesCount = likeRepository.countByPostId(post.getId());
                    boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
                    long commentsCount = commentRepository.countByPostId(post.getId());
                    long repostsCount = repostRepository.countByOriginalPostId(post.getId());
                    boolean reposted = repostRepository.existsByUserIdAndOriginalPostId(userId, post.getId());

                    return new PostResponseDTO(
                            post.getId(),
                            post.getContent(),
                            post.getImageUrl(),
                            post.getCreatedAt(),
                            user.getId(),
                            user.getUsername(),
                            user.getProfilePicture(),
                            likesCount,
                            liked,
                            commentsCount,
                            repostsCount,
                            reposted
                    );
                }).toList();
    }

    public Repost repost(UUID postId, UUID userId) {
        boolean jaRepostou = repostRepository.existsByUserIdAndOriginalPostId(userId, postId);

        if (jaRepostou) {
            throw new RuntimeException("Você já repostou este post");
        }

        Post original = postRepository.findById(postId).orElseThrow();

        Repost repost = Repost.builder()
                .userId(userId)
                .originalPostId(postId)
                .createdAt(LocalDateTime.now())
                .build();

        repostRepository.save(repost);

        if (!original.getUserId().equals(userId)) {
            notificationService.create(original.getUserId(), userId, "mention", "repostou seu post.");
        }

        return repost;
    }

    public void removeRepost(UUID postId, UUID userId) {
        Repost repost = repostRepository
                .findByUserIdAndOriginalPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("Repost não encontrado"));
        repostRepository.delete(repost);
    }

    public List<PostResponseDTO> getExplorePosts(UUID userId) {
        List<UUID> followingIds = new ArrayList<>(
                followRepository.findByFollowerId(userId)
                        .stream()
                        .map(f -> f.getFollowingId())
                        .toList()
        );
        followingIds.add(userId);

        List<Post> posts = postRepository.findAll()
                .stream()
                .filter(p -> !followingIds.contains(p.getUserId()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(20)
                .toList();

        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElseThrow();
            long likesCount = likeRepository.countByPostId(post.getId());
            boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
            long commentsCount = commentRepository.countByPostId(post.getId());
            long repostsCount = repostRepository.countByOriginalPostId(post.getId());
            boolean reposted = repostRepository.existsByUserIdAndOriginalPostId(userId, post.getId());

            return new PostResponseDTO(
                    post.getId(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getCreatedAt(),
                    user.getId(),
                    user.getUsername(),
                    user.getProfilePicture(),
                    likesCount,
                    liked,
                    commentsCount,
                    repostsCount,
                    reposted
            );
        }).toList();
    }

    public void toggleLike(UUID postId, UUID userId) {
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            likeRepository.deleteByUserIdAndPostId(userId, postId);
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setPostId(postId);
            likeRepository.save(like);

            Post post = postRepository.findById(postId).orElseThrow();
            if (!post.getUserId().equals(userId)) {
                notificationService.create(
                        post.getUserId(), userId, "like", "curtiu seu post."
                );
            }
        }
    }
}