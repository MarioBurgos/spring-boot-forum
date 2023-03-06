package api.forum.repository.posts;

import api.forum.model.posts.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1, category2;
    @BeforeEach
    void setUp() {
        category1 = new Category("category1");
        category2 = new Category("category2");
        categoryRepository.saveAll(List.of(category1, category2));
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    void findByTitle_ValidTitle_CategoryFound() {
        Optional<Category> optionalCategory = categoryRepository.findByTitle("category1");
        assertEquals(optionalCategory.get().getTitle(), "category1");
    }
}