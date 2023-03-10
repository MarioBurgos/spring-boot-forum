package api.forum.controller.impl.users;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.model.users.Role;
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

    private Admin superAdmin, firstRegularAdmin, secondRegularAdmin;
    private String superAdminUsername, firstRegularAdminAUsername, secondRegularAdminBUsername, superAdminPassword, firstRegularAdminAPassword, secondRegularAdminBPassword;
    private MvcResult mvcResult;

    @BeforeEach
    void setUp() {
        superAdminUsername = "super-admin";
        firstRegularAdminAUsername = "admin A";
        secondRegularAdminBUsername = "admin B";
        superAdminPassword = "password";
        firstRegularAdminAPassword = "password";
        secondRegularAdminBPassword = "password";
        superAdmin = new Admin(superAdminUsername, "EMAIL", passwordEncoder.encode(superAdminPassword));
        superAdmin.setShift(Shift.MORNING);
        superAdmin.setLocation("UK");
        superAdmin.setStatus(Status.DISCONNECTED);
        superAdmin.setLastLogIn(Date.valueOf(LocalDate.now()));
        superAdmin.setRoles(List.of(new Role("SUPERADMIN"), new Role("ADMIN")));
        firstRegularAdmin = new Admin(firstRegularAdminAUsername, "emailA@email", passwordEncoder.encode(firstRegularAdminAPassword));
        firstRegularAdmin.setShift(Shift.NIGHT);
        firstRegularAdmin.setLocation("ITA");
        firstRegularAdmin.setStatus(Status.ON_VACATION);
        firstRegularAdmin.setLastLogIn(Date.valueOf(LocalDate.of(2020, 1, 1)));
        secondRegularAdmin = new Admin(secondRegularAdminBUsername, "emailB@email", passwordEncoder.encode(secondRegularAdminBPassword));
        secondRegularAdmin.setShift(Shift.NIGHT);
        secondRegularAdmin.setLocation("ITA");
        secondRegularAdmin.setStatus(Status.ON_LINE);
        secondRegularAdmin.setLastLogIn(Date.valueOf(LocalDate.of(2023, 3, 3)));
        adminRepository.saveAll(List.of(superAdmin, firstRegularAdmin, secondRegularAdmin));
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void findAll_HasNoSuperAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins")
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findAll_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins")
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(3, jsonArray.length());
        assertTrue(jsonArray.get(0).toString().contains(superAdmin.getUsername()));
        assertTrue(jsonArray.get(1).toString().contains(firstRegularAdmin.getLocation()));
        assertTrue(jsonArray.get(2).toString().contains(secondRegularAdmin.getLocation()));
    }

    @Test
    void findById_HasAdminRoleButItIsNotHisOrHerAdminAccount_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/id/" + secondRegularAdmin.getId())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findById_AuthenticatedAdmin_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/id/" + firstRegularAdmin.getId())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void findById_AuthenticatedSuperAdmin_ResponseStatus200Ok() throws Exception {
        // Give SUPERADMIN Role and persist
        superAdmin.setRoles(List.of(new Role("ADMIN"), new Role("SUPERADMIN")));
        adminRepository.save(superAdmin);
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        // find admin2 by id
        mvcResult = mockMvc.perform(get("/admins/id/" + firstRegularAdmin.getId())
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonObject.toString().contains(firstRegularAdmin.getUsername()));
        assertTrue(jsonObject.toString().contains(firstRegularAdmin.getEmail()));
    }

    @Test
    void findByEmail_HasAdminRoleButItIsNotHisOrHerAdminAccount_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/email/" + secondRegularAdmin.getEmail())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findByEmail_AuthenticatedSuperAdmin_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/email/" + firstRegularAdmin.getEmail())
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonObject.toString().contains(firstRegularAdmin.getUsername()));
        assertTrue(jsonObject.toString().contains(firstRegularAdmin.getEmail()));
    }

    @Test
    void findByEmail_AuthenticatedRegularAdmin_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/email/" + firstRegularAdmin.getEmail())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonObject.toString().contains(firstRegularAdmin.getUsername()));
        assertTrue(jsonObject.toString().contains(firstRegularAdmin.getEmail()));
    }

    @Test
    void findByLastLogInGreaterThan_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/last-login/2010-03-03")
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findByLastLogInGreaterThan_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/last-login/2023-3-3")
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(2, jsonArray.length());
        assertTrue(jsonArray.get(0).toString().contains(superAdmin.getUsername()));
        assertTrue(jsonArray.get(0).toString().contains(superAdmin.getEmail()));
        assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getUsername()));
        assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getLocation()));
    }

    @Test
    void findByLastLogInBetween_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/last-login?start-date=2020-1-1&end-date=2023-3-10")
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void findByLastLogInBetween_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/last-login?start-date=2023-3-5&end-date=2023-3-10")
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(1, jsonArray.length());
        assertTrue(jsonArray.get(0).toString().contains(superAdmin.getUsername()));
        assertTrue(jsonArray.get(0).toString().contains(superAdmin.getEmail()));

    }
 @Test
    void findByLastLogIn_SuperAdminPassesBothStartDateAndEndDateParams_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/last-login?start-date=2023-3-1&end-date=2023-3-5")
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
     JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
     assertEquals(1, jsonArray.length());
     assertTrue(jsonArray.get(0).toString().contains(secondRegularAdmin.getUsername()));
     assertTrue(jsonArray.get(0).toString().contains(secondRegularAdmin.getEmail()));

    }
@Test
    void findByLastLogIn_SuperAdminPassesOnlyStartDateParam_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/last-login?start-date=2023-1-1")
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
     JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
     assertEquals(2, jsonArray.length());
     assertTrue(jsonArray.get(0).toString().contains(superAdmin.getUsername()));
     assertTrue(jsonArray.get(0).toString().contains(superAdmin.getEmail()));
     assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getUsername()));
     assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getEmail()));
    }
    @Test
    void findByLastLogIn_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/last-login?start-date=2020-1-1&end-date=2023-3-10")
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findByStatus_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/status/" + secondRegularAdmin.getStatus())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void findByStatus_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/status/" + secondRegularAdmin.getStatus())
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(1, jsonArray.length());
        assertTrue(jsonArray.get(0).toString().contains(secondRegularAdmin.getUsername()));
        assertTrue(jsonArray.get(0).toString().contains(secondRegularAdmin.getEmail()));
    }

    @Test
    void findByShift_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/shift/" + secondRegularAdmin.getShift())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void findByShift_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/shift/" + secondRegularAdmin.getShift())
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(2, jsonArray.length());
        assertTrue(jsonArray.get(0).toString().contains(firstRegularAdmin.getUsername()));
        assertTrue(jsonArray.get(0).toString().contains(firstRegularAdmin.getEmail()));
        assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getUsername()));
        assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getEmail()));
    }

    @Test
    void findByLocation_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminAUsername)
                        .param("password", firstRegularAdminAPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/location/" + secondRegularAdmin.getLocation())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void findByLocation_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(get("/admins/location/" + firstRegularAdmin.getLocation())
                        .headers(httpHeaders)
                        .param("username", superAdmin.getUsername())
                        .param("password", superAdmin.getPassword()))
                .andExpect(status().isOk())
                .andReturn();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        for (int i = 0; i < jsonArray.length(); i++) {
            System.out.println(jsonArray.get(i));
        }
        assertEquals(2, jsonArray.length());
        assertTrue(jsonArray.get(0).toString().contains(firstRegularAdmin.getUsername()));
        assertTrue(jsonArray.get(0).toString().contains(firstRegularAdmin.getEmail()));
        assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getUsername()));
        assertTrue(jsonArray.get(1).toString().contains(secondRegularAdmin.getEmail()));
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