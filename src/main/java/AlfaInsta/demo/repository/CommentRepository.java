package AlfaInsta.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import AlfaInsta.demo.model.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    long countByPostId(UUID postId);
    List<Comment> findByPostIdOrderByCreatedAtAsc(UUID postId);

}
