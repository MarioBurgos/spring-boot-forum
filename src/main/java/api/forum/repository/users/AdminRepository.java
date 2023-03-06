package api.forum.repository.users;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.model.users.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUserName(String userName);
    Optional<Admin> findByEmail(String email);
    List<Admin> findByLastLoggedInGreaterThan(Date date);
    List<Admin> findByStatus(Status status);
    List<Admin> findByShift(Shift shift);
    List<Admin> findByLocation(String location);
}
