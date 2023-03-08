package api.forum.controller.interfaces;

import api.forum.controller.dto.AdminDTO;
import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;

import java.sql.Date;
import java.util.List;

public interface AdminController {
    List<AdminDTO> findAll();

    AdminDTO findById(Integer id);

    AdminDTO findByUserName(String userName);

    AdminDTO findByEmail(String email);

    List<AdminDTO> findByLastLogInGreaterThan(Date date);

    List<AdminDTO> findByLastLogInBetween(Date start, Date end);

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
