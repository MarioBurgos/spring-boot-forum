package api.forum.controller.dto;

import api.forum.model.posts.Post;

import java.util.List;

public class CategoryDTO {
    private Integer id;
    private List<Post> posts;
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
