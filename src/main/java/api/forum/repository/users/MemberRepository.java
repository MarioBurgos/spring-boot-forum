package api.forum.repository.users;

import api.forum.model.enums.Status;
import api.forum.model.users.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUsername(String userName);
    Optional<Member> findByEmail(String email);
    List<Member> findByLastLogInGreaterThan(Date date);
    List<Member> findByStatus(Status status);
    List<Member> findByRegistrationDateGreaterThan(Date date);
    List<Member> findByMembershipLevelGreaterThan(Integer membershipLevel);
}
