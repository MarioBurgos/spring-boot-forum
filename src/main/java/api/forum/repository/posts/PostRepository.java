package api.forum.repository.posts;

import api.forum.model.posts.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Optional<Post> findByTitleContaining(String title);
    List<Post> findByAuthorUsername(String authorUsername);
    List<Post> findByCategoryTitle(String categoryTitle);
    List<Post> findByDateGreaterThanEqual(Date date);
    List<Post> findByDateBetween(Date start, Date end);
}
