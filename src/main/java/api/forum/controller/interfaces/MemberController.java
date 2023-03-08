package api.forum.controller.interfaces;

import api.forum.model.enums.Status;
import api.forum.model.users.Member;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MemberController {
    Optional<Member> findByUserName(String userName);
    Optional<Member> findByEmail(String email);
    List<Member> findByLastLogInGreaterThanEqual(Date date);
    List<Member> findByStatus(Status status);
    List<Member> findByRegistrationDateGreaterThan(Date date);
    List<Member> findByMembershipLevelGreaterThan(Integer membershipLevel);
}
