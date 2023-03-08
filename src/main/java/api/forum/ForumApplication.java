package api.forum;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.posts.Category;
import api.forum.model.posts.Comment;
import api.forum.model.posts.Post;
import api.forum.model.users.Admin;
import api.forum.model.users.Member;
import api.forum.repository.posts.CategoryRepository;
import api.forum.repository.posts.CommentRepository;
import api.forum.repository.posts.PostRepository;
import api.forum.repository.users.AdminRepository;
import api.forum.repository.users.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


@SpringBootApplication
public class ForumApplication implements CommandLineRunner {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Admin admin1, admin2;
    private Member member1, member2, member3, member4;
    private Category category1, category2;
    private Post post1, post2, post3, post4;
    private Comment parentComment1, parentComment2, parentComment3, parentComment4, sonComment1, sonComment2, sonComment3, sonComment4;


    public static void main(String[] args) {
        SpringApplication.run(ForumApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        populateDatabase();
    }

    private void populateDatabase(){
        /**
         * Inserts
         */
        // Admins
        admin1 = new Admin("admin1 name", "admin1@email.com", "admin1pass");
        admin1.setStatus(Status.DISCONNECTED);
        admin1.setShift(Shift.MORNING);
        admin1.setLocation("CAT");
        admin1.setLastLogIn(Date.valueOf(LocalDate.of(2023, 3, 1)));

        admin2 = new Admin("admin2 name", "admin2@email.com", "admin2pass");
        admin2.setStatus(Status.ON_VACATION);
        admin2.setShift(Shift.EVENING);
        admin2.setLocation("UK");
        admin2.setLastLogIn(Date.valueOf(LocalDate.of(2022, 12, 20)));
        //save
        adminRepository.saveAll(List.of(admin1, admin2));

        // Members
        member1 = new Member("member1 name", "member1@email.com", "member1pass", "url_img1");
        member1.setMembershipLevel(1);
        member1.setRegistrationDate(Date.valueOf(LocalDate.of(2011, 1, 11)));
        member1.setStatus(Status.DISCONNECTED);
        member1.setLastLogIn(Date.valueOf(LocalDate.of(2023, 1, 1)));

        member2 = new Member("member2 name", "member2@email.com", "member2pass", "url_img2");
        member2.setMembershipLevel(2);
        member2.setRegistrationDate(Date.valueOf(LocalDate.of(2020, 2, 22)));
        member2.setStatus(Status.DISCONNECTED);
        member2.setLastLogIn(Date.valueOf(LocalDate.now()));

        member3 = new Member("member3 name", "member3@email.com", "member3pass", "url_img3");
        member3.setMembershipLevel(3);
        member3.setRegistrationDate(Date.valueOf(LocalDate.of(2023, 3, 1)));
        member3.setStatus(Status.PENDING_CONFIRMATION);
        member3.setLastLogIn(null);

        member4 = new Member("member4 name", "member4@email.com", "member4pass", "url_img4");
        member4.setMembershipLevel(4);
        member4.setRegistrationDate(Date.valueOf(LocalDate.of(2014, 4, 4)));
        member4.setStatus(Status.BANNED);
        member4.setLastLogIn(Date.valueOf(LocalDate.of(2019, 6, 23)));
        //save
        memberRepository.saveAll(List.of(member1, member2, member3, member4));

        // Categories
        category1 = new Category("category 1");
        category2 = new Category("category 2");
        //save
        categoryRepository.saveAll(List.of(category1, category2));

        // Posts
        post1 = new Post(category1, member1, "Post1 title", "Post1 content");
        post1.setDate(Date.valueOf(LocalDate.of(2022, 8, 15)));
        post1.setUpvotes(1245);
        post1.setDownvotes(300);
        post2 = new Post(category2, member2, "Post2 title", "Post2 content");
        post2.setUpvotes(15);
        post2.setDownvotes(25);
        post3 = new Post(category1, member3, "Post3 title", "Post3 content");
        post3.setDate(Date.valueOf(LocalDate.of(2022, 12, 25)));
        post3.setUpvotes(100);
        post3.setDownvotes(600);
        post4 = new Post(category2, member4, "Post4 title", "Post4 content");
        //save
        postRepository.saveAll(List.of(post1, post2, post3, post4));

        // Comments
        parentComment1 = new Comment(post1, member1, "parentComment1 content");
        parentComment1.setUpvote(2);
        parentComment2 = new Comment(post2, member2, "parentComment2 content");
        parentComment2.setDownvote(1);
        parentComment3 = new Comment(post3, member3, "parentComment3 content");
        parentComment3.setUpvote(5);
        parentComment3.setDownvote(2);
        parentComment4 = new Comment(post4, member4, "parentComment4 content");
        sonComment1 = new Comment(post1, member4, "sonComment1 content");
        sonComment1.setParentComment(parentComment1);
        sonComment2 = new Comment(post2, member3, "sonComment2 content");
        sonComment2.setParentComment(parentComment2);
        sonComment3 = new Comment(post3, member2, "sonComment3 content");
        sonComment3.setParentComment(parentComment3);
        sonComment4 = new Comment(post4, member1, "sonComment4 content");
        sonComment4.setParentComment(parentComment4);
        commentRepository.saveAll(
                List.of(
                        parentComment1,
                        parentComment2,
                        parentComment3,
                        parentComment4,
                        sonComment1,
                        sonComment2,
                        sonComment3,
                        sonComment4)
        );

    }
}


