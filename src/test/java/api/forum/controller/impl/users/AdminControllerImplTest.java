package api.forum.controller.impl.users;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.model.users.Role;
import api.forum.repository.users.AdminRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
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
    private String admin1Username, admin2Username, password1, password2;
    private MvcResult mvcResult;

    @BeforeEach
    void setUp() {
        admin1Username = "username1";
        admin2Username = "username2";
        password1 = "PASSWORD";
        password2 = "password";
        admin1 = new Admin(admin1Username, "EMAIL", passwordEncoder.encode(password1));
        admin1.setShift(Shift.MORNING);
        admin1.setLocation("UK");
        admin1.setStatus(Status.DISCONNECTED);
        admin1.setLastLogIn(Date.valueOf(LocalDate.now()));
        admin2 = new Admin(admin2Username, "email@email", passwordEncoder.encode(password2));
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
    void findAll_NotAuthenticatedSuperAdmin_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", admin1Username)
                        .param("password", password1))
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
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findAll_ValidSuperAdmin_ResponseStatus200Ok() throws Exception {
        // Give SUPERADMIN Role and persist
        admin1.setRoles(List.of(new Role("ADMIN"), new Role("SUPERADMIN")));
        adminRepository.save(admin1);
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", admin1Username)
                        .param("password", password1))
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
    void findById_NotAuthenticatedAdmin_ResponseStatus404NotFound() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", admin1Username)
                        .param("password", password1))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/id/" + admin2.getId())
                        .headers(httpHeaders)
                        .param("username", admin1.getUsername())
                        .param("password", admin1.getPassword()))
                .andExpect(status().isNotFound())
                .andReturn();
    }
    @Test
    void findById_AuthenticatedAdmin_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", admin1Username)
                        .param("password", password1))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/id/" + admin1.getId())
                        .headers(httpHeaders)
                        .param("username", admin1.getUsername())
                        .param("password", admin1.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void findById_AuthenticatedSuperAdmin_ResponseStatus200Ok() throws Exception {
        // Give SUPERADMIN Role and persist
        admin1.setRoles(List.of(new Role("ADMIN"), new Role("SUPERADMIN")));
        adminRepository.save(admin1);
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", admin1Username)
                        .param("password", password1))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        // find admin2 by id
        mvcResult = mockMvc.perform(get("/admins/id/" + admin2.getId())
                        .headers(httpHeaders)
                        .param("username", admin1.getUsername())
                        .param("password", admin1.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonObject.toString().contains(admin2.getUsername()));
    }

    void findByUserName_ValidSuperAdmin_ResponseStatus200Ok(){}
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