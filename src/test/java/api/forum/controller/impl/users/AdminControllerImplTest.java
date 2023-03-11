package api.forum.controller.impl.users;

import api.forum.model.enums.Shift;
import api.forum.model.enums.Status;
import api.forum.model.users.Admin;
import api.forum.model.users.Role;
import api.forum.repository.users.AdminRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDTOControllerImplTest {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Admin superAdmin, firstRegularAdmin, secondRegularAdmin, thirdRegularAdmin;
    private String superAdminUsername, firstRegularAdminUsername, secondRegularAdminUsername, superAdminPassword, firstRegularAdminPassword, secondRegularAdminPassword;
    private MvcResult mvcResult;

    @BeforeEach
    void setUp() {
        superAdminUsername = "super-admin";
        firstRegularAdminUsername = "admin A";
        secondRegularAdminUsername = "admin B";
        superAdminPassword = "password";
        firstRegularAdminPassword = "password";
        secondRegularAdminPassword = "password";
        superAdmin = new Admin(superAdminUsername, "EMAIL", passwordEncoder.encode(superAdminPassword));
        superAdmin.setShift(Shift.MORNING);
        superAdmin.setLocation("UK");
        superAdmin.setStatus(Status.DISCONNECTED);
        superAdmin.setLastLogIn(Date.valueOf(LocalDate.now()));
        superAdmin.setRoles(List.of(new Role("SUPERADMIN"), new Role("ADMIN")));
        firstRegularAdmin = new Admin(firstRegularAdminUsername, "emailA@email", passwordEncoder.encode(firstRegularAdminPassword));
        firstRegularAdmin.setShift(Shift.NIGHT);
        firstRegularAdmin.setLocation("ITA");
        firstRegularAdmin.setStatus(Status.BANNED);
        firstRegularAdmin.setLastLogIn(Date.valueOf(LocalDate.of(2020, 1, 1)));
        secondRegularAdmin = new Admin(secondRegularAdminUsername, "emailB@email", passwordEncoder.encode(secondRegularAdminPassword));
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
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
        assertEquals(jsonArray.getJSONObject(0).getString("username"), superAdmin.getUsername());
        assertEquals(jsonArray.getJSONObject(1).getString("username"), firstRegularAdmin.getUsername());
        assertEquals(jsonArray.getJSONObject(2).getString("username"), secondRegularAdmin.getUsername());
    }

    @Test
    void findById_HasAdminRoleButItIsNotHisOrHerAdminAccount_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
        jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertEquals(jsonObject.getString("username"), firstRegularAdmin.getUsername());
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
        assertEquals(jsonObject.getString("username"), firstRegularAdmin.getUsername());
        assertEquals(jsonObject.getString("email"), firstRegularAdmin.getEmail());
    }

    @Test
    void findByEmail_HasAdminRoleButItIsNotHisOrHerAdminAccount_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
        assertEquals(jsonObject.getString("username"), firstRegularAdmin.getUsername());
        assertEquals(jsonObject.getString("email"), firstRegularAdmin.getEmail());
    }

    @Test
    void findByEmail_AuthenticatedRegularAdmin_ResponseStatus200Ok() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
        assertEquals(jsonObject.getString("username"), firstRegularAdmin.getUsername());
        assertEquals(jsonObject.getString("email"), firstRegularAdmin.getEmail());
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
     assertEquals(jsonArray.getJSONObject(0).getString("username"), secondRegularAdmin.getUsername());
     assertEquals(jsonArray.getJSONObject(0).getString("username"), secondRegularAdmin.getUsername());

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
    assertEquals(jsonArray.getJSONObject(0).getString("username"), superAdmin.getUsername());
    assertEquals(jsonArray.getJSONObject(0).getString("email"), superAdmin.getEmail());
    assertEquals(jsonArray.getJSONObject(1).getString("username"), secondRegularAdmin.getUsername());
    assertEquals(jsonArray.getJSONObject(1).getString("email"), secondRegularAdmin.getEmail());
    }
    @Test
    void findByLastLogIn_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
        assertEquals(jsonArray.getJSONObject(0).getString("username"), firstRegularAdmin.getUsername());
        assertEquals(jsonArray.getJSONObject(0).getString("email"), firstRegularAdmin.getEmail());
        assertEquals(jsonArray.getJSONObject(1).getString("username"), secondRegularAdmin.getUsername());
        assertEquals(jsonArray.getJSONObject(1).getString("email"), secondRegularAdmin.getEmail());
    }

    @Test
    void findByLocation_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
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
        assertEquals(2, jsonArray.length());
        assertEquals(jsonArray.getJSONObject(0).getString("username"), firstRegularAdmin.getUsername());
        assertEquals(jsonArray.getJSONObject(0).getString("email"), firstRegularAdmin.getEmail());
        assertEquals(jsonArray.getJSONObject(1).getString("username"), secondRegularAdmin.getUsername());
        assertEquals(jsonArray.getJSONObject(1).getString("email"), secondRegularAdmin.getEmail());
    }
    @Test
    void addNewAdmin_RegularAdminTriesToCreateNewAdmin_ResponseStatus403Forbidden() throws Exception {
        // new Admin
        thirdRegularAdmin = new Admin("newSuperAdmin", "newEmail", passwordEncoder.encode("password"));
        thirdRegularAdmin.setShift(Shift.EVENING);
        thirdRegularAdmin.setLocation("BCN");
        thirdRegularAdmin.setStatus(Status.BANNED);
        thirdRegularAdmin.setLastLogIn(Date.valueOf(LocalDate.of(2010,8,22)));
        String body = objectMapper.writeValueAsString(thirdRegularAdmin);
        // login as SuperAdmin
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(
                        post("/admins")
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void addNewAdmin_SuperAdminCreatesNewAdmin_ResponseStatus201Created() throws Exception {
        // new Admin
        thirdRegularAdmin = new Admin("newAdmin", "newEmail", "password");
        String body = objectMapper.writeValueAsString(thirdRegularAdmin);
        // login as SuperAdmin
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(
                        post("/admins")
                                .headers(httpHeaders)
                                .param("username", superAdmin.getUsername())
                                .param("password", superAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertEquals(jsonObject.getString("username"), superAdmin.getUsername());
        assertEquals(jsonObject.getString("email"), superAdmin.getEmail());
    }

    @Test
    void updateAdmin_RegularAdminTriesToUpdateAnotherAdmin_ResponseStatus403Forbidden() throws Exception {
        secondRegularAdmin.setLocation("updated");
        secondRegularAdmin.setEmail("updated");
        String body = objectMapper.writeValueAsString(secondRegularAdmin);
        // login as admin1
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        put("/admins/" + secondRegularAdmin.getId())
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void updateAdmin_RegularAdminTriesToUpdateHisOrHerOwnInformation_ResponseStatus403Forbidden() throws Exception {
        firstRegularAdmin.setRoles(List.of(new Role("SUPERADMIN"), new Role("ADMIN")));
        firstRegularAdmin.setShift(Shift.MORNING);
        firstRegularAdmin.setStatus(Status.ON_VACATION);
        String body = objectMapper.writeValueAsString(firstRegularAdmin);
        // login as admin1
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        put("/admins/" + firstRegularAdmin.getId())
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void updateAdmin_SuperAdminTriesToUpdateAnotherAdmin_ResponseStatus204NoContent() throws Exception {
        firstRegularAdmin.setRoles(List.of(new Role("SUPERADMIN"), new Role("ADMIN")));
        firstRegularAdmin.setShift(Shift.MORNING);
        firstRegularAdmin.setStatus(Status.ON_VACATION);
        String body = objectMapper.writeValueAsString(firstRegularAdmin);
        // login as admin1
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", superAdminUsername)
                        .param("password", superAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        put("/admins/" + firstRegularAdmin.getId())
                                .headers(httpHeaders)
                                .param("username", superAdmin.getUsername())
                                .param("password", superAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Admin> optionalAdmin = adminRepository.findById(firstRegularAdmin.getId());
        assertTrue(optionalAdmin.isPresent());
        assertEquals(optionalAdmin.get().getStatus(), firstRegularAdmin.getStatus());
        assertEquals(optionalAdmin.get().getShift(), firstRegularAdmin.getShift());
    }

    @Test
    void updateAdminEmail_RegularAdminTriesToUpdateAnotherAdminSEmail_ResponseStatus403Forbidden() throws Exception {
        secondRegularAdmin.setEmail("updated");
        String body = objectMapper.writeValueAsString(secondRegularAdmin);
        // login as admin1
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", firstRegularAdminUsername)
                        .param("password", firstRegularAdminPassword))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        put("/admins/" + secondRegularAdmin.getId() + "/email")
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void updateAdminPassword_RegularAdminTriesToUpdateOwnEmail_ResponseStatus401NoContent() {
    }

    @Test
    void updateAdminPassword_SuperAdminTriesToUpdateOtherEmail_ResponseStatus401NoContent() {
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