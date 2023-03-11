package api.forum.controller.impl.posts;

import api.forum.controller.dto.postDTO.CategoryDTO;
import api.forum.controller.dto.postDTO.ContentDTO;
import api.forum.controller.dto.postDTO.PostDTO;
import api.forum.controller.dto.postDTO.TitleDTO;
import api.forum.controller.interfaces.PostController;
import api.forum.model.posts.Post;
import api.forum.service.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class PostControllerImpl implements PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public List<PostDTO> findAll() {
        return postService.findAll();
    }

    @GetMapping("/posts/title/{title}")
    public PostDTO findByTitleContaining(@PathVariable String title) {
        return postService.findByTitleContaining(title);
    }

    @GetMapping("/posts/author/{authorUsername}")
    public List<PostDTO> findByAuthorUsername(@PathVariable String authorUsername) {
        return postService.findByAuthorUsername(authorUsername);
    }

    @GetMapping("/posts/category/{categoryTitle}")
    public List<PostDTO> findByCategoryTitle(@PathVariable String categoryTitle) {
        return postService.findByCategoryTitle(categoryTitle);
    }

    @GetMapping("/posts/date")
    public List<PostDTO> findByDate(@RequestParam(name = "start-date") Date startDate, @RequestParam(name = "end-date") Optional<Date> endDate) {
        return postService.findByDate(startDate, endDate);
    }

    @Override
    public Post add(Post post) {
        return postService.add(post);
    }

    @Override
    public void update(Long id, PostDTO postDTO) {
        postService.update(id, postDTO);
    }

    @Override
    public void updateTitle(Long id, TitleDTO titleDTO) {
        postService.updateTitle(id, titleDTO);
    }

    @Override
    public void updateCategory(Long id, CategoryDTO categoryDTO) {
        postService.updateCategory(id, categoryDTO);
    }

    @Override
    public void updateContent(Long id, ContentDTO contentDTO) {
        postService.updateContent(id, contentDTO);
    }

    @Override
    public void delete(Long id) {
        postService.delete(id);
    }

    @Override
    public void upvote(Long id) {
        postService.upvote(id);
    }

    @Override
    public void downvote(Long id) {
        postService.downvote(id);
    }

}
