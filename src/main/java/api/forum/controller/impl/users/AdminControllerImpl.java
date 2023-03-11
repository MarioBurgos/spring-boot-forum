package api.forum.controller.impl.users;

import api.forum.controller.dto.userDTO.*;
import api.forum.controller.interfaces.AdminController;
import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.service.interfaces.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class AdminControllerImpl implements AdminController {
    @Autowired
    private AdminService adminService;

    // GET MAPPING
    @GetMapping("/admins")
    public List<AdminDTO> findAll() {
        return adminService.findAll();
    }

    @GetMapping("/admins/{id}")
    public AdminDTO findById(@PathVariable Integer id, Authentication authentication) {
        return adminService.findById(id, authentication);
    }

    @GetMapping("/admins/username/{username}")
    public AdminDTO findByUserName(@PathVariable String userName, Authentication authentication) {
        return adminService.findByUserName(userName, authentication);
    }

    @GetMapping("/admins/email/{email}")
    public AdminDTO findByEmail(@PathVariable String email, Authentication authentication) {
        return adminService.findByEmail(email, authentication);
    }

    @GetMapping("/admins/last-login")
    public List<AdminDTO> findByLastLogIn(@RequestParam(name = "start-date") Date startDate, @RequestParam(name = "end-date") Optional<Date> endDate) {
        return adminService.findByLastLogIn(startDate, endDate);
    }


    @GetMapping("/admins/status/{status}")
    public List<AdminDTO> findByStatus(@PathVariable Status status) {
        return adminService.findByStatus(status);
    }

    @GetMapping("/admins/shift/{shift}")
    public List<AdminDTO> findByShift(@PathVariable Shift shift) {
        return adminService.findByShift(shift);
    }

    @GetMapping("/admins/location/{location}")
    public List<AdminDTO> findByLocation(@PathVariable String location) {
        return adminService.findByLocation(location);
    }

    // POST MAPPING
    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public Admin addNewAdmin(@RequestBody Admin admin) {
        return adminService.addNewAdmin(admin);
    }

    // PUT MAPPING
    @PutMapping("/admins/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAdmin(@PathVariable Integer id, @RequestBody Admin admin) {
        adminService.updateAdmin(id, admin);
    }

    // PATCH MAPPING
    @PatchMapping("/admins/{id}/username")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAdminUsername(@PathVariable Integer id, @RequestBody UsernameDTO usernameDTO, Authentication authentication) {
        adminService.updateAdminUsername(id, usernameDTO, authentication);
    }
    @PatchMapping("/admins/{id}/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAdminEmail(@PathVariable Integer id, @RequestBody EmailDTO emailDTO, Authentication authentication) {
        adminService.updateAdminEmail(id, emailDTO, authentication);
    }

    @PatchMapping("/admins/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAdminPassword(@PathVariable Integer id, @RequestBody PasswordDTO passwordDTO, Authentication authentication) {
        adminService.updateAdminPassword(id, passwordDTO, authentication);
    }

    @PatchMapping("/admins/{id}/last-login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLastLogIn(@PathVariable Integer id, @RequestBody Date lastLogIn) {
        adminService.updateLastLogIn(id, lastLogIn);
    }

    @PatchMapping("/admins/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable Integer id, @RequestBody StatusDTO statusDTO, Authentication authentication) {
        adminService.updateStatus(id, statusDTO, authentication);
    }

    @PatchMapping("/admins/{id}/shift")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateShift(@PathVariable Integer id, @RequestBody ShiftDTO shiftDTO) {
        adminService.updateShift(id, shiftDTO);
    }

    @PatchMapping("/admins/{id}/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLocation(@PathVariable Integer id, @RequestBody LocationDTO locationDTO, Authentication authentication) {
        adminService.updateLocation(id, locationDTO, authentication);
    }

    @DeleteMapping("/admins/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        adminService.delete(id);
    }


}
