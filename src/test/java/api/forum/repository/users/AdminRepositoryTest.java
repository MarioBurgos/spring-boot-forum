package api.forum.repository.users;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.model.users.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;
    private Admin admin, banned;
    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setUserName("admin");
        admin.setEmail("email");
        admin.setLastLoggedIn(Date.valueOf(LocalDate.of(2015, 01, 01)));
        admin.setStatus(Status.DISCONNECTED);
        admin.setShift(Shift.NIGHT);
        admin.setLocation("UK");

        banned = new Admin();
        banned.setUserName("banned");
        banned.setEmail("emailbanned");
        banned.setLastLoggedIn(Date.valueOf(LocalDate.of(2000, 01, 01)));
        banned.setStatus(Status.BANNED);
        banned.setShift(Shift.NIGHT);
        banned.setLocation("UK");

        adminRepository.saveAll(List.of(admin, banned));
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void findByUserName_GivenAValidName_ReturnsAdminFound() {
        Optional<Admin>optionalAdmin = adminRepository.findByUserName(admin.getUserName());
        assertEquals(admin.getId(), optionalAdmin.get().getId());
        assertNotEquals(banned.getId(), optionalAdmin.get().getId());
        // find another
        optionalAdmin = adminRepository.findByUserName(banned.getUserName());
        assertEquals(banned.getId(), optionalAdmin.get().getId());
        assertNotEquals(admin.getId(), optionalAdmin.get().getId());
    }

    @Test
    void findByEmail_GivenAValidEmail_ReturnsAdminFound() {
        Optional<Admin>optionalAdmin = adminRepository.findByEmail(admin.getEmail());
        assertEquals(admin.getId(), optionalAdmin.get().getId());
        assertNotEquals(banned.getId(), optionalAdmin.get().getId());
        // find another
        optionalAdmin = adminRepository.findByEmail(banned.getEmail());
        assertEquals(banned.getId(), optionalAdmin.get().getId());
        assertNotEquals(admin.getId(), optionalAdmin.get().getId());
    }

    @Test
    void findByLastLoggedInGreaterThan_GivenDateLowerThanExisting_ReturnsMembersLoggedAfterTheDate() {
        List<Admin> admins = adminRepository.findByLastLoggedInGreaterThan(Date.valueOf(LocalDate.of(2010,1,1)));
        assertEquals(1, admins.size());
        assertNotEquals(banned.getId(), admins.get(0).getId());
        // find another
        admins = adminRepository.findByLastLoggedInGreaterThan(Date.valueOf(LocalDate.of(1990,1,1)));
        assertEquals(2, admins.size());
        assertEquals(admin.getId(), admins.get(0).getId());
        assertEquals(banned.getId(), admins.get(1).getId());
    }

    @Test
    void findByStatus_GivenAnExistingStatus_ReturnsAListOfAdminsWithThatStatus() {
        List<Admin> disconnectedAdmins = adminRepository.findByStatus(Status.DISCONNECTED);
        List<Admin> bannedAdmins = adminRepository.findByStatus(Status.BANNED);
        assertEquals(1, disconnectedAdmins.size());
        assertEquals(1, bannedAdmins.size());
        assertEquals(admin.getId(), disconnectedAdmins.get(0).getId());
        assertEquals(banned.getId(), bannedAdmins.get(0).getId());
    }
    @Test
    void findByShift_GivenAValidShift_ReturnsAListOfAdminsThatWorkOnThatShift() {
        List<Admin> nightShiftAdmins = adminRepository.findByShift(Shift.NIGHT);
        assertEquals(2, nightShiftAdmins.size());
        assertEquals(admin.getId(), nightShiftAdmins.get(0).getId());
    }

    @Test
    void findByLocation_GivenAValidLocation_ReturnsAListOfAdminsInThatLocation() {
        List<Admin> adminsFromUK = adminRepository.findByLocation("UK");
        assertEquals(2, adminsFromUK.size());
        assertEquals(admin.getId(), adminsFromUK.get(0).getId());
        assertEquals("UK", adminsFromUK.get(0).getLocation());
        assertEquals("UK", adminsFromUK.get(1).getLocation());
    }
}