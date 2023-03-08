package api.forum.controller.impl.users;

import api.forum.controller.interfaces.MemberController;
import api.forum.model.enums.Status;
import api.forum.model.users.Member;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MemberControllerImpl implements MemberController {
    @Override
    public Optional<Member> findByUserName(String userName) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<Member> findByLastLogInGreaterThanEqual(Date date) {
        return null;
    }

    @Override
    public List<Member> findByStatus(Status status) {
        return null;
    }

    @Override
    public List<Member> findByRegistrationDateGreaterThan(Date date) {
        return null;
    }

    @Override
    public List<Member> findByMembershipLevelGreaterThan(Integer membershipLevel) {
        return null;
    }
}
