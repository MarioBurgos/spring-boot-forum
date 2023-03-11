package api.forum.service.impl;

import api.forum.controller.dto.userDTO.*;
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
        // search by id
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        AdminDTO adminDTO = null;
        if (optionalAdmin.isPresent()) {
            // check whether the user has the same username that the Admin we're looking for
            // or if the user has Role = "SUPERADMIN"
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches");
        admin.setId(id);
        adminRepository.save(admin);
    }

    @Override
    public void updateAdminUsername(Integer id, UsernameDTO usernameDTO, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                optionalAdmin.get().setUsername(usernameDTO.getUsername());
                adminRepository.save(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches");
        }
    }

    @Override
    public void updateAdminEmail(Integer id, EmailDTO emailDTO, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                optionalAdmin.get().setEmail(emailDTO.getEmail());
                adminRepository.save(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches");
        }
    }

    @Override
    public void updateAdminPassword(Integer id, PasswordDTO passwordDTO, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                optionalAdmin.get().setPassword(passwordDTO.getPassword());
                adminRepository.save(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches");
        }
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
    public void updateStatus(Integer id, StatusDTO statusDTO, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                optionalAdmin.get().setStatus(statusDTO.getStatus());
                adminRepository.save(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches");
        }
    }

    @Override
    public void updateShift(Integer id, ShiftDTO shiftDTO) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches for " + id);
        optionalAdmin.get().setShift(shiftDTO.getShift());
        adminRepository.save(optionalAdmin.get());
    }

    @Override
    public void updateLocation(Integer id, LocationDTO locationDTO, Authentication authentication) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            if (authentication.getName().equals(optionalAdmin.get().getUsername())
                    || authentication.getAuthorities().toString().contains("SUPERADMIN") ) {
                optionalAdmin.get().setLocation(locationDTO.getLocation());
                adminRepository.save(optionalAdmin.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches");
        }
    }

    @Override
    public void delete(Integer id) {
        if (adminRepository.findById(id).isPresent())
            adminRepository.deleteById(id);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matches");
    }


    /**
     * PRIVATE METHODS
     */
    private AdminDTO createSingleDTO(Admin admin) {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(admin.getId());
        adminDTO.setRoles(admin.getRoles());
        adminDTO.setUsername(admin.getUsername());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setLastLogIn(admin.getLastLogIn());
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
