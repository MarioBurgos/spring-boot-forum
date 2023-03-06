package api.forum.repository.posts;

import api.forum.model.posts.Category;
import api.forum.model.posts.Post;
import api.forum.model.users.Admin;
import api.forum.model.users.Member;
import api.forum.repository.users.AdminRepository;
import api.forum.repository.users.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AdminRepository adminRepository;

    private Admin admin;
    private Member proMember, regularMember;
    private Category category1, category2, category3;
    private Post post1, post2, post3;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        adminRepository.save(admin);
        proMember = new Member();
        regularMember = new Member();
        memberRepository.saveAll(List.of(proMember, regularMember));

        category1 = new Category("category1");
        category2 = new Category("category2");
        category3 = new Category("category3");
        categoryRepository.saveAll(List.of(category1, category2, category3));

        post1 = new Post(category1, proMember, "title1", "content1");
        post1.setDate(Date.valueOf(LocalDate.of(1999, 12, 12)));
        post2 = new Post(category2, proMember, "title2", "content2");
        post2.setDate(Date.valueOf(LocalDate.of(2010, 6, 6)));
        post3 = new Post(category3, admin, "title3", "content3");
        post3.setDate(Date.valueOf(LocalDate.of(2020, 1, 1)));
        postRepository.saveAll(List.of(post1, post2, post3));
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        adminRepository.deleteAll();
        memberRepository.deleteAll();

    }

    @Test
    void findByAuthor_GivenAValidAuthor_ReturnsPostFound() {
        List<Post> proMemberPosts = postRepository.findByAuthor(proMember);
        List<Post> regularMemberPosts = postRepository.findByAuthor(regularMember);
        List<Post> adminPosts = postRepository.findByAuthor(admin);

        assertEquals(2, proMemberPosts.size());
        assertEquals(0, regularMemberPosts.size());
        assertEquals(1, adminPosts.size());
        assertEquals(proMember.getId(), proMemberPosts.get(0).getAuthor().getId());
        assertEquals(proMember.getId(), proMemberPosts.get(1).getAuthor().getId());
        assertEquals(admin.getId(), adminPosts.get(0).getAuthor().getId());
        assertEquals(post1.getTitle(), proMemberPosts.get(0).getTitle());
        assertEquals(post2.getTitle(), proMemberPosts.get(1).getTitle());
        assertEquals(post3.getTitle(), adminPosts.get(0).getTitle());
    }

    @Test
    void findByTitle_GivenAValidTitle_ReturnsThePostFound() {
        Optional<Post> optionalPost1 = postRepository.findByTitle("title1");
        Optional<Post> optionalPost2 = postRepository.findByTitle("title2");
        Optional<Post> optionalPost3 = postRepository.findByTitle("title3");
        assertTrue(optionalPost1.isPresent() && optionalPost2.isPresent() && optionalPost3.isPresent());
    }

    @Test
    void findByCategory_GivenAValidCategory_ReturnsThePostsInThatCategory() {
        List<Post> postsByCategory1 = postRepository.findByCategory(category1);
        List<Post> postsByCategory2 = postRepository.findByCategory(category2);
        List<Post> postsByCategory3 = postRepository.findByCategory(category3);
        assertEquals(1, postsByCategory1.size());
        assertEquals(1, postsByCategory2.size());
        assertEquals(1, postsByCategory3.size());
        assertEquals(post1.getTitle(), postsByCategory1.get(0).getTitle());
        assertEquals(post2.getAuthor().getId(), postsByCategory1.get(0).getAuthor().getId());
        assertEquals(post3.getDate(), postsByCategory3.get(0).getDate());
    }

    @Test
    void findByDateGreaterThan_GivenDateLowerThanExisting_ReturnsPostsCreatedAfterTheDate() {
        List<Post> posts = postRepository.findByDateGreaterThan(Date.valueOf(LocalDate.of(2000,1,1)));
        assertEquals(2, posts.size());
        assertEquals(post2.getDate(), posts.get(0).getDate());
        assertNotEquals(post3.getDate(), posts.get(0).getDate());
        assertEquals(post3.getDate(), posts.get(1).getDate());
    }
}