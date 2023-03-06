package api.forum.repository.posts;

import api.forum.model.posts.Category;
import api.forum.model.posts.Post;
import api.forum.model.users.Admin;
import api.forum.model.users.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Optional<Post> findByTitle(String title);
    List<Post> findByAuthor(Member author);
    List<Post> findByAuthor(Admin author);
    List<Post> findByCategory(Category category);
    List<Post> findByDateGreaterThan(Date date);


}
