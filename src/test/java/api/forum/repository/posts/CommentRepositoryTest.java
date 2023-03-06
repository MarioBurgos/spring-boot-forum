package api.forum.repository.posts;

import api.forum.model.posts.Category;
import api.forum.model.posts.Comment;
import api.forum.model.posts.Post;
import api.forum.model.users.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    private Comment comment1, comment2, comment3;
    private Post post;
    @BeforeEach
    void setUp() {
        post = new Post();
        postRepository.save(post);
        comment1 = new Comment();
        comment1.setPost(post);
        comment1.setContent("first comment");
        commentRepository.save(comment1);
        // a comment whose parent comment is comment1
        comment2 = new Comment();
        comment2.setPost(post);
        comment2.setParentComment(comment1);
        comment2.setContent("second comment, son of first");
        commentRepository.save(comment2);
        // a comment whose parent comment is comment2, it should set comment1
        comment3 = new Comment();
        comment3.setPost(post);
        comment3.setParentComment(comment2);
        comment3.setContent("third comment, son of second but should set son of first");
        commentRepository.save(comment3);
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll(); //on cascade
    }

    @Test
    void findByPostId_GivenAnExistingPostId_ReturnsAllComments() {
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        assertEquals(3, comments.size());
        assertEquals(comment1.getContent(), comments.get(0).getContent());

    }

    @Test
    void findByParentComment_GivenAnExistingParentComment_ReturnsAllSons() {
        Comment parentComment = commentRepository.findByPostId(post.getId()).get(0);
        List<Comment> comments = commentRepository.findByParentComment(comment1);
        assertEquals(comments.get(0).getParentComment().getContent(), parentComment.getContent());
        assertEquals(comments.get(1).getParentComment().getContent(), parentComment.getContent());
    }
}