package api.forum.repository.posts;

import api.forum.model.posts.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByParentComment(Comment parentComment);
}
