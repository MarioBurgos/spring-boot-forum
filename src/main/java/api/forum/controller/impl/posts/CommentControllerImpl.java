package api.forum.controller.impl.posts;

import api.forum.controller.interfaces.CommentController;
import api.forum.model.posts.Comment;

import java.util.List;

public class CommentControllerImpl implements CommentController {
    @Override
    public List<Comment> findByPostId(Long postId) {
        return null;
    }

    @Override
    public List<Comment> findByParentComment(Comment parentComment) {
        return null;
    }
}
