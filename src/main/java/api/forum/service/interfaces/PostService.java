package api.forum.service.interfaces;

import api.forum.controller.dto.postDTO.CategoryDTO;
import api.forum.controller.dto.postDTO.ContentDTO;
import api.forum.controller.dto.postDTO.PostDTO;
import api.forum.controller.dto.postDTO.TitleDTO;
import api.forum.model.posts.Category;
import api.forum.model.posts.Post;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface PostService {
    List<PostDTO> findAll();
    PostDTO findByTitleContaining(String title);
    List<PostDTO> findByAuthorUsername(String authorUsername);
    List<PostDTO> findByCategoryTitle(String categoryTitle);
    List<PostDTO> findByDate(Date startDate, Optional<Date> endDate);
    Post add(Post post);
    void delete(Long id);
    void update(Long id, PostDTO postDTO);
    void updateTitle(Long id, TitleDTO titleDTO);
    void updateCategory(Long id, CategoryDTO categoryDTO);
    void updateContent(Long id, ContentDTO contentDTO);
    void upvote(Long id);
    void downvote(Long id);

}
