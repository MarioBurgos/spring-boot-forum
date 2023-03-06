package api.forum.repository.users;

import api.forum.model.users.Admin;
import api.forum.model.users.Member;
import api.forum.model.users.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Admin admin;
    private Member member;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        member = new Member();
        adminRepository.save(admin);
        memberRepository.save(member);
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void findByName_GivenAnExistingRoleName_ReturnsRole() {
        List<Role> roles = roleRepository.findByName("ADMIN");
        assertEquals(roles.get(0).getName(), admin.getRoles().get(0).getName());
    }
}