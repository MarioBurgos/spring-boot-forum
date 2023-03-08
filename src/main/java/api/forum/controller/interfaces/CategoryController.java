package api.forum.controller.interfaces;

import api.forum.model.posts.Category;

import java.util.Optional;

public interface CategoryController {
    Optional<Category> findByTitle(String title);

}
