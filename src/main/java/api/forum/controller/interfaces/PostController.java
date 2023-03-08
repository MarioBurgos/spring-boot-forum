package api.forum.controller.interfaces;

import api.forum.model.posts.Category;
import api.forum.model.posts.Post;
import api.forum.model.users.Admin;
import api.forum.model.users.Member;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface PostController {
    Optional<Post> findByTitle(String title);
    List<Post> findByAuthor(Member author);
    List<Post> findByAuthor(Admin author);
    List<Post> findByCategory(Category category);
    List<Post> findByDateGreaterThan(Date date);
}
