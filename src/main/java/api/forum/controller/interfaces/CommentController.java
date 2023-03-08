package api.forum.controller.interfaces;

import api.forum.model.posts.Comment;

import java.util.List;

public interface CommentController {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByParentComment(Comment parentComment);
}
