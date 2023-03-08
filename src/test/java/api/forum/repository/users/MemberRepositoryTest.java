package api.forum.repository.users;

import api.forum.model.enums.Status;
import api.forum.model.users.Member;
import api.forum.repository.posts.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    private Member member, duplicatedMember;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setUsername("username");
        member.setEmail("email");
        member.setLastLogIn(Date.valueOf(LocalDate.now()));
        member.setRegistrationDate(Date.valueOf(LocalDate.now()));
        member.setMembershipLevel(10);
        member.setStatus(Status.ON_LINE);

        duplicatedMember = new Member();
        duplicatedMember.setLastLogIn(Date.valueOf(LocalDate.now()));
        duplicatedMember.setRegistrationDate(Date.valueOf(LocalDate.now()));
        duplicatedMember.setMembershipLevel(10);
        duplicatedMember.setStatus(Status.ON_LINE);

        memberRepository.saveAll(List.of(member, duplicatedMember));
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void findByUserName_GivenAnExistingUsername_ReturnsMemberFound() {
        Optional<Member> optionalMember = memberRepository.findByUsername(member.getUsername());
        assertEquals(member.getId(), optionalMember.get().getId());
        assertEquals(member.getMembershipLevel(), optionalMember.get().getMembershipLevel());
        assertEquals(member.getRegistrationDate(), optionalMember.get().getRegistrationDate());
        System.out.println(member.getRegistrationDate());
    }

    @Test
    void findByEmail_GivenAnExistingEmail_ReturnsMemberFound() {
        Optional<Member> members = memberRepository.findByEmail(member.getEmail());
        assertEquals(member.getId(), members.get().getId());
        assertEquals(member.getMembershipLevel(), members.get().getMembershipLevel());
        assertEquals(member.getRegistrationDate(), members.get().getRegistrationDate());
    }

    @Test
    void findByLastLoggedInGreaterThan_GivenDateLowerThanExisting_ReturnsMembersLoggedAfterTheDate() {
        List<Member> members = memberRepository.findByLastLogInGreaterThan(Date.valueOf(LocalDate.of(2022, 01, 01)));
        assertEquals(2, members.size());
        assertEquals(member.getId(), members.get(0).getId());
        assertEquals(duplicatedMember.getId(), members.get(1).getId());
    }

    @Test
    void findByStatus_GivenAnExistingStatus_ReturnsAListOfMembersWithThatStatus() {
        List<Member> members = memberRepository.findByStatus(Status.ON_LINE);
        assertEquals(2, members.size());
        assertEquals(member.getId(), members.get(0).getId());
        assertEquals(duplicatedMember.getId(), members.get(1).getId());
    }

    @Test
    void findByRegistrationDateGreaterThan_GivenDateLowerThanExisting_ReturnsMembersRegisteredAfterTheDate() {
        List<Member> members = memberRepository.findByRegistrationDateGreaterThan(Date.valueOf(LocalDate.of(2020, 01, 01)));
        assertEquals(2, members.size());
        assertEquals(member.getId(), members.get(0).getId());
        assertEquals(duplicatedMember.getId(), members.get(1).getId());
    }

    @Test
    void findByMembershipLevelGreaterThan_GivenAMembershipLevel_ReturnsMembersWithHigherMembershipLevel() {
        List<Member> members = memberRepository.findByMembershipLevelGreaterThan(1);
        assertEquals(2, members.size());
        assertEquals(member.getId(), members.get(0).getId());
        assertEquals(duplicatedMember.getId(), members.get(1).getId());

        members = memberRepository.findByMembershipLevelGreaterThan(15);
        assertEquals(0, members.size());
    }
}