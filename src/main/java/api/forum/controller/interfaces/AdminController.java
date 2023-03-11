package api.forum.controller.interfaces;

import api.forum.controller.dto.userDTO.*;
import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AdminController {
    List<AdminDTO> findAll();

    AdminDTO findById(Integer id, Authentication authentication);

    AdminDTO findByUserName(String userName, Authentication authentication);

    AdminDTO findByEmail(String email, Authentication authentication);

    List<AdminDTO> findByLastLogIn(Date startDate, Optional<Date> endDate);

    List<AdminDTO> findByStatus(Status status);

    List<AdminDTO> findByShift(Shift shift);

    List<AdminDTO> findByLocation(String location);

    Admin addNewAdmin(Admin admin);

    void updateAdmin(Integer id, Admin admin);

    void updateAdminUsername(Integer id, UsernameDTO usernameDTO, Authentication authentication);

    void updateAdminEmail(Integer id, EmailDTO emailDTO, Authentication authentication);

    void updateAdminPassword(Integer id, PasswordDTO passwordDTO, Authentication authentication);

    void updateLastLogIn(Integer id, Date lastLogIn);

    void updateStatus(Integer id, StatusDTO statusDTO, Authentication authentication);

    void updateShift(Integer id, ShiftDTO shiftDTO);

    void updateLocation(Integer id, LocationDTO locationDTO, Authentication authentication);
    void delete(Integer id);
}
