package api.forum.service.interfaces;

import api.forum.controller.dto.AdminDTO;
import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<AdminDTO> findAll();

    AdminDTO findById(Integer id, Authentication authentication);

    AdminDTO findByUserName(String userName, Authentication authentication);

    AdminDTO findByEmail(String email, Authentication authentication);

    List<AdminDTO> findByLastLogIn(Date start, Optional<Date> end);

    List<AdminDTO> findByStatus(Status status);

    List<AdminDTO> findByShift(Shift shift);

    List<AdminDTO> findByLocation(String location);

    Admin addNewAdmin(Admin admin);

    void updateAdmin(Integer id, Admin admin);

    void updateAdminEmail(Integer id, String email);

    void updateAdminPassword(Integer id, String password);

    void updateLastLogIn(Integer id, Date lastLogIn);

    void updateStatus(Integer id, Status status);

    void updateShift(Integer id, Shift shift);

    void updateLocation(Integer id, String location);
}
