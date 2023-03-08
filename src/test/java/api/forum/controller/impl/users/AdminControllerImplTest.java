package api.forum.controller.impl.users;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.repository.users.AdminRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerImplTest {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Admin admin1, admin2;
    private MvcResult mvcResult;

    @BeforeEach
    void setUp() {
        admin1 = new Admin("IMANADMIN", "EMAIL", passwordEncoder.encode("PASSWORD"));
        admin1.setShift(Shift.MORNING);
        admin1.setLocation("UK");
        admin1.setStatus(Status.DISCONNECTED);
        admin1.setLastLogIn(Date.valueOf(LocalDate.now()));
        admin2 = new Admin("anotherone", "email@email", passwordEncoder.encode("PASSWORD"));
        admin2.setShift(Shift.NIGHT);
        admin2.setLocation("ITA");
        admin2.setStatus(Status.ON_VACATION);
        admin2.setLastLogIn(Date.valueOf(LocalDate.of(2023, 1, 1)));
        adminRepository.saveAll(List.of(admin1, admin2));
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void findAll_UserIsNotSuperAdmin_401() throws Exception {

    }

    @Test
    void findAll() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", "IMANADMIN")
                        .param("password", "PASSWORD"))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

        mvcResult = mockMvc.perform(get("/admins")
                        .headers(httpHeaders)
                        .param("username", admin1.getUsername())
                        .param("password", admin1.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonArray.get(0).toString().contains(admin1.getUsername()));
        assertTrue(jsonArray.get(1).toString().contains(admin2.getLocation()));
    }

    @Test
    void findById() {
    }

    @Test
    void findByUserName() {
    }

    @Test
    void findByEmail() {
    }

    @Test
    void findByLastLogInGreaterThan() {
    }

    @Test
    void findByLastLogInBetween() {
    }

    @Test
    void findByStatus() {
    }

    @Test
    void findByShift() {
    }

    @Test
    void findByLocation() {
    }

    @Test
    void addNewAdmin() {
    }

    @Test
    void updateAdmin() {
    }

    @Test
    void updateAdminEmail() {
    }

    @Test
    void updateAdminPassword() {
    }

    @Test
    void updateLastLogIn() {
    }

    @Test
    void updateStatus() {
    }

    @Test
    void updateShift() {
    }

    @Test
    void updateLocation() {
    }
}