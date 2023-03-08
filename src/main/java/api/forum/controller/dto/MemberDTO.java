package api.forum.controller.dto;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.posts.Post;
import api.forum.model.users.Log;
import api.forum.model.users.Role;

import java.sql.Date;
import java.util.List;

public class MemberDTO {
    private Integer id;
    private List<Role> roles;
    private String userName;
    private String email;
    private String password;
    private Date lastLoggedIn;
    private Status status;
    private List<Post> posts;
    private String profilePicture;
    private Date registrationDate;
    private Integer membershipLevel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(Integer membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
}
