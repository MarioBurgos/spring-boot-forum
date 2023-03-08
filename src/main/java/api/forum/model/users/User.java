package api.forum.model.users;

import api.forum.model.enums.Status;
import api.forum.model.posts.Post;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "user_id")
    private List<Role> roles;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private Date lastLogIn;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(
            mappedBy = "author",
            cascade = CascadeType.ALL
    )
    private List<Post> posts;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        lastLogIn = null;
        status = Status.PENDING_CONFIRMATION;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Date getLastLogIn() {
        return lastLogIn;
    }

    public void setLastLogIn(Date lastLogIn) {
        this.lastLogIn = lastLogIn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
