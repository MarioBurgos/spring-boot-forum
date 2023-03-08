package api.forum.repository.users;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUsername(String userName);
    Optional<Admin> findByEmail(String email);
    List<Admin> findByLastLogInGreaterThanEqual(Date date);
    List<Admin> findByLastLogInBetween(Date start, Date end);
    List<Admin> findByStatus(Status status);
    List<Admin> findByShift(Shift shift);
    List<Admin> findByLocation(String location);
}
