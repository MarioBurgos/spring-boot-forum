package api.forum.controller.impl.posts;

import api.forum.controller.interfaces.CategoryController;
import api.forum.model.posts.Category;

import java.util.Optional;

public class CategoryControllerImpl implements CategoryController {
    @Override
    public Optional<Category> findByTitle(String title) {
        return Optional.empty();
    }
}
