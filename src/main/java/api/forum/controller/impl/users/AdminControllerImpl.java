package api.forum.controller.impl.users;

import api.forum.controller.dto.AdminDTO;
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

@RestController
public class AdminControllerImpl implements AdminController {
    @Autowired
    private AdminService adminService;

    // GET MAPPING
    @GetMapping("/admins")
    public List<AdminDTO> findAll() {
        return adminService.findAll();
    }

    @GetMapping("/admins/id/{id}")
    public AdminDTO findById(@PathVariable Integer id, Authentication authentication) {
        return adminService.findById(id, authentication);
    }

    @GetMapping("/admins/username/{username}")
    public AdminDTO findByUserName(@PathVariable String userName) {
        return adminService.findByUserName(userName);
    }

    @GetMapping("/admins/email/{email}")
    public AdminDTO findByEmail(@PathVariable String email) {
        return adminService.findByEmail(email);
    }

    @GetMapping("/admins/last-login/{last-login}")
    public List<AdminDTO> findByLastLogInGreaterThan(@PathVariable Date lastLogin) {
        return adminService.findByLastLogInGreaterThanEqual(lastLogin);
    }

    @GetMapping("/admins/last-login")
    public List<AdminDTO> findByLastLogInBetween(@RequestParam Date start, @RequestParam Date end) {
        return adminService.findByLastLogInBetween(start, end);
    }


    @GetMapping("/admins/{status}")
    public List<AdminDTO> findByStatus(@PathVariable Status status) {
        return adminService.findByStatus(status);
    }

    @GetMapping("/admins/{shift}")
    public List<AdminDTO> findByShift(@PathVariable Shift shift) {
        return adminService.findByShift(shift);
    }

    @GetMapping("/admins/{location}")
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
    public void updateAdmin(@PathVariable Integer id, @RequestBody Admin admin) {
        adminService.updateAdmin(id, admin);
    }

    // PATCH MAPPING
    @PatchMapping("/admins/{id}/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAdminEmail(@PathVariable Integer id, @RequestBody String email) {
        adminService.updateAdminEmail(id, email);
    }

    @PatchMapping("/admins/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAdminPassword(@PathVariable Integer id, @RequestBody String password) {
        adminService.updateAdminPassword(id, password);
    }

    @PatchMapping("/admins/{id}/last-login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLastLogIn(@PathVariable Integer id, @RequestBody Date lastLogIn) {
        adminService.updateLastLogIn(id, lastLogIn);
    }

    @PatchMapping("/admins/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable Integer id, @RequestBody Status status) {
        adminService.updateStatus(id, status);
    }

    @PatchMapping("/admins/{id}/shift")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateShift(@PathVariable Integer id, @RequestBody Shift shift) {
        adminService.updateShift(id, shift);
    }

    @PatchMapping("/admins/{id}/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLocation(@PathVariable Integer id, @RequestBody String location) {
        adminService.updateLocation(id, location);
    }

}
