package api.forum.service.impl;

import api.forum.controller.dto.AdminDTO;
import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.repository.users.AdminRepository;
import api.forum.service.interfaces.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public List<AdminDTO> findAll() {
        List<Admin> admins = adminRepository.findAll();
        return createListOfDTO(admins);
    }

    @Override
    public AdminDTO findById(Integer id, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        AdminDTO adminDTO = null;
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                adminDTO = createSingleDTO(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for ID: " + id);
        }
        return adminDTO;
    }

    @Override
    public AdminDTO findByUserName(String userName, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(userName);
        AdminDTO adminDTO = null;
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                adminDTO = createSingleDTO(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for username: " + userName);
        }
        return adminDTO;
    }

    @Override
    public AdminDTO findByEmail(String email, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        AdminDTO adminDTO = null;
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                adminDTO = createSingleDTO(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for email: " + email);
        }
        return adminDTO;
    }

    @Override
    public List<AdminDTO> findByLastLogIn(Date startDate, Optional<Date> endDate) {
        List<Admin> admins;

        if (endDate.isPresent()){
            admins = adminRepository.findByLastLogInBetween(startDate, endDate.get());
        }else {
            admins = adminRepository.findByLastLogInGreaterThanEqual(startDate);
        }
        if (admins.size() > 0)
            return createListOfDTO(admins);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches between " + startDate.toString() + " and " + endDate.toString());
    }


    @Override
    public List<AdminDTO> findByStatus(Status status) {
        List<Admin> admins = adminRepository.findByStatus(status);
        if (admins.size() > 0)
            return createListOfDTO(admins);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for status: " + status.toString());
    }

    @Override
    public List<AdminDTO> findByShift(Shift shift) {
        List<Admin> admins = adminRepository.findByShift(shift);
        if (admins.size() > 0)
            return createListOfDTO(admins);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for shift: " + shift.toString());
    }

    @Override
    public List<AdminDTO> findByLocation(String location) {
        List<Admin> admins = adminRepository.findByLocation(location);
        if (admins.size() > 0)
            return createListOfDTO(admins);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for location: " + location);
    }

    @Override
    public Admin addNewAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    @Override
    public void updateAdmin(Integer id, Admin admin) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        admin.setId(id);
        adminRepository.save(admin);
    }

    @Override
    public void updateAdminEmail(Integer id, String email) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        optionalAdmin.get().setEmail(email);
        adminRepository.save(optionalAdmin.get());
    }

    @Override
    public void updateAdminPassword(Integer id, String password) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        optionalAdmin.get().setPassword(password);
        adminRepository.save(optionalAdmin.get());
    }

    @Override
    public void updateLastLogIn(Integer id, Date lastLogIn) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        optionalAdmin.get().setLastLogIn(lastLogIn);
        adminRepository.save(optionalAdmin.get());
    }

    @Override
    public void updateStatus(Integer id, Status status) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        optionalAdmin.get().setStatus(status);
        adminRepository.save(optionalAdmin.get());
    }

    @Override
    public void updateShift(Integer id, Shift shift) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        optionalAdmin.get().setShift(shift);
        adminRepository.save(optionalAdmin.get());
    }

    @Override
    public void updateLocation(Integer id, String location) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        optionalAdmin.get().setLocation(location);
        adminRepository.save(optionalAdmin.get());
    }

    /**
     * PRIVATE METHODS
     */
    private AdminDTO createSingleDTO(Admin admin) {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(admin.getId());
        adminDTO.setRoles(admin.getRoles());
        adminDTO.setUserName(admin.getUsername());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setLastLoggedIn(admin.getLastLogIn());
        adminDTO.setStatus(admin.getStatus());
        adminDTO.setShift(admin.getShift());
        adminDTO.setLocation(admin.getLocation());
        return adminDTO;
    }

    private List<AdminDTO> createListOfDTO(List<Admin> admins) {
        List<AdminDTO> adminDTOS = new ArrayList<>();
        AdminDTO adminDTO;
        for (Admin admin : admins) {
            adminDTO = createSingleDTO(admin);
            adminDTOS.add(adminDTO);
        }
        return adminDTOS;
    }
}
