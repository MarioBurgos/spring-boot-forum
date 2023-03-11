package api.forum.service.impl;

import api.forum.controller.dto.postDTO.CategoryDTO;
import api.forum.controller.dto.postDTO.ContentDTO;
import api.forum.controller.dto.postDTO.PostDTO;
import api.forum.controller.dto.postDTO.TitleDTO;
import api.forum.model.posts.Post;
import api.forum.repository.posts.CategoryRepository;
import api.forum.repository.posts.PostRepository;
import api.forum.service.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<PostDTO> findAll() {
        List<Post> posts = postRepository.findAll();
        return createListOfDTO(posts);
    }

    @Override
    public PostDTO findByTitleContaining(String title) {
        Optional<Post> optionalPost = postRepository.findByTitleContaining(title);
        if (optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results");
        else
            return createSingleDTO(optionalPost.get());
    }

    @Override
    public List<PostDTO> findByAuthorUsername(String authorUsername) {
        List<Post> posts = postRepository.findByAuthorUsername(authorUsername);
        if (posts.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results");
        else
            return createListOfDTO(posts);
    }

    @Override
    public List<PostDTO> findByCategoryTitle(String categoryTitle) {
        List<Post> posts = postRepository.findByCategoryTitle(categoryTitle);
        if (posts.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results");
        else
            return createListOfDTO(posts);
    }

    @Override
    public List<PostDTO> findByDate(Date startDate, Optional<Date> endDate) {
        List<Post> posts;
        if (endDate.isPresent()){
            posts = postRepository.findByDateBetween(startDate, endDate.get());
        }else {
            posts = postRepository.findByDateGreaterThanEqual(startDate);
        }
        if (posts.size() > 0)
            return createListOfDTO(posts);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches between " + startDate + " and " + endDate.get());
    }

    @Override
    public Post add(Post post) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void update(Long id, PostDTO postDTO) {

    }

    @Override
    public void updateTitle(Long id, TitleDTO titleDTO) {

    }

    @Override
    public void updateCategory(Long id, CategoryDTO categoryDTO) {

    }

    @Override
    public void updateContent(Long id, ContentDTO contentDTO) {

    }

    @Override
    public void upvote(Long id) {

    }

    @Override
    public void downvote(Long id) {

    }

    /**
     * PRIVATE METHODS
     */
    private PostDTO createSingleDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setCategory(post.getCategory());
        postDTO.setAuthor(post.getAuthor());
        postDTO.setComments(post.getComments());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setUpvotes(post.getUpvotes());
        postDTO.setDownvotes(post.getDownvotes());
        postDTO.setDate(post.getDate());
        return postDTO;
    }

    private List<PostDTO> createListOfDTO(List<Post> posts) {
        List<PostDTO> postDTOS = new ArrayList<>();
        PostDTO postDTO;
        for (Post post : posts) {
            postDTO = createSingleDTO(post);
            postDTOS.add(postDTO);
        }
        return postDTOS;
    }
}
