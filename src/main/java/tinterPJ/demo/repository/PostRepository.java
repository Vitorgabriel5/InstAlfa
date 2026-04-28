package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tinterPJ.demo.model.Post;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByUserIdInOrderByCreatedAtDesc(List<UUID> userIds);
}
