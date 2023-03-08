package api.forum.controller.impl.posts;

import api.forum.controller.interfaces.PostController;
import api.forum.model.posts.Category;
import api.forum.model.posts.Post;
import api.forum.model.users.Admin;
import api.forum.model.users.Member;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class PostControllerImpl implements PostController {
    @Override
    public Optional<Post> findByTitle(String title) {
        return Optional.empty();
    }

    @Override
    public List<Post> findByAuthor(Member author) {
        return null;
    }

    @Override
    public List<Post> findByAuthor(Admin author) {
        return null;
    }

    @Override
    public List<Post> findByCategory(Category category) {
        return null;
    }

    @Override
    public List<Post> findByDateGreaterThan(Date date) {
        return null;
    }
}
