package api.forum.controller.impl.users;

import api.forum.controller.dto.userDTO.*;
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
import java.util.Optional;

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
    private MvcResult mvcResult;

    private Admin superAdmin, firstRegularAdmin, secondRegularAdmin, thirdRegularAdmin;
    private String superAdminUsername, firstRegularAdminUsername, secondRegularAdminUsername, superAdminPassword, firstRegularAdminPassword, secondRegularAdminPassword;

    @BeforeEach
    void setUp() {
        superAdminUsername = "super-admin";
        firstRegularAdminUsername = "first";
        secondRegularAdminUsername = "second";
        superAdminPassword = "password";
        firstRegularAdminPassword = "password";
        secondRegularAdminPassword = "password";
        superAdmin = new Admin(superAdminUsername, "super@email", passwordEncoder.encode(superAdminPassword));
        superAdmin.setShift(Shift.MORNING);
        superAdmin.setLocation("superCity");
        superAdmin.setStatus(Status.DISCONNECTED);
        superAdmin.setLastLogIn(Date.valueOf(LocalDate.now()));
        superAdmin.setRoles(List.of(new Role("SUPERADMIN"), new Role("ADMIN")));
        firstRegularAdmin = new Admin(firstRegularAdminUsername, "first@email", passwordEncoder.encode(firstRegularAdminPassword));
        firstRegularAdmin.setShift(Shift.NIGHT);
        firstRegularAdmin.setLocation("firstCity");
        firstRegularAdmin.setStatus(Status.BANNED);
        firstRegularAdmin.setLastLogIn(Date.valueOf(LocalDate.of(2020, 1, 1)));
        secondRegularAdmin = new Admin(secondRegularAdminUsername, "second@email", passwordEncoder.encode(secondRegularAdminPassword));
        secondRegularAdmin.setShift(Shift.NIGHT);
        secondRegularAdmin.setLocation("firstCity");
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(get("/admins")
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findAll_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(get("/admins/" + secondRegularAdmin.getId())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findById_AuthenticatedAdmin_ResponseStatus200Ok() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        JSONObject jsonObject;
        mvcResult = mockMvc.perform(get("/admins/" + firstRegularAdmin.getId())
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        JSONObject jsonObject;
        mvcResult = mockMvc.perform(get("/admins/" + firstRegularAdmin.getId())
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(get("/admins/email/" + secondRegularAdmin.getEmail())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findByEmail_AuthenticatedSuperAdmin_ResponseStatus200Ok() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        JSONObject jsonObject;
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        JSONObject jsonObject;
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
     HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
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
    HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(get("/admins/last-login?start-date=2020-1-1&end-date=2023-3-10")
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void findByStatus_HasRegularAdminRole_ResponseStatus403Forbidden() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(get("/admins/status/" + secondRegularAdmin.getStatus())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void findByStatus_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(get("/admins/shift/" + secondRegularAdmin.getShift())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void findByShift_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(get("/admins/location/" + secondRegularAdmin.getLocation())
                        .headers(httpHeaders)
                        .param("username", firstRegularAdmin.getUsername())
                        .param("password", firstRegularAdmin.getPassword()))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void findByLocation_HasSuperAdminRole_ResponseStatus200Ok() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
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
        // login as regular admin
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
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
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        JSONObject jsonObject;
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
        // login as regular admin
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
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
    void updateAdmin_RegularAdminUpdatesOwnInformation_ResponseStatus403Forbidden() throws Exception {
        firstRegularAdmin.setRoles(List.of(new Role("SUPERADMIN"), new Role("ADMIN")));
        firstRegularAdmin.setShift(Shift.MORNING);
        firstRegularAdmin.setStatus(Status.ON_VACATION);
        String body = objectMapper.writeValueAsString(firstRegularAdmin);
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
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
    void updateAdmin_SuperAdminUpdatesAnotherAdmin_ResponseStatus204NoContent() throws Exception {
        firstRegularAdmin.setRoles(List.of(new Role("SUPERADMIN"), new Role("ADMIN")));
        firstRegularAdmin.setShift(Shift.MORNING);
        firstRegularAdmin.setStatus(Status.ON_VACATION);
        String body = objectMapper.writeValueAsString(firstRegularAdmin);
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
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
    void updateAdminUsername_RegularAdminTriesToUpdateAnotherAdminSUsername_ResponseStatus403Forbidden() throws Exception {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("updated");
        String body = objectMapper.writeValueAsString(usernameDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        patch("/admins/" + secondRegularAdmin.getId() + "/username")
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
    void updateAdminUsername_RegularAdminUpdatesOwnUsername_ResponseStatus204NoContent() throws Exception {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("updated");
        String body = objectMapper.writeValueAsString(usernameDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/username")
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Admin> optionalAdmin = adminRepository.findById(firstRegularAdmin.getId());
        assertTrue(optionalAdmin.isPresent());
        assertEquals(optionalAdmin.get().getUsername(), usernameDTO.getUsername());
    }
    @Test
    void updateAdminUsername_SuperAdminUpdatesOtherUsername_ResponseStatus204NoContent() throws Exception {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("updated");
        String body = objectMapper.writeValueAsString(usernameDTO);
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/username")
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
        assertEquals(optionalAdmin.get().getUsername(), usernameDTO.getUsername());
    }

    @Test
    void updateAdminEmail_RegularAdminTriesToUpdateAnotherAdminSEmail_ResponseStatus403Forbidden() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("updated");
        String body = objectMapper.writeValueAsString(emailDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        patch("/admins/" + secondRegularAdmin.getId() + "/email")
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
    void updateAdminEmail_RegularAdminUpdatesOwnEmail_ResponseStatus204NoContent() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("updated");
        String body = objectMapper.writeValueAsString(emailDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/email")
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Admin> optionalAdmin = adminRepository.findById(firstRegularAdmin.getId());
        assertTrue(optionalAdmin.isPresent());
        assertEquals(optionalAdmin.get().getEmail(), emailDTO.getEmail());
    }
    @Test
    void updateAdminEmail_SuperAdminUpdatesOtherEmail_ResponseStatus204NoContent() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("updated");
        String body = objectMapper.writeValueAsString(emailDTO);
        // login as super admin
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/email")
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
        assertEquals(optionalAdmin.get().getEmail(), emailDTO.getEmail());
    }
    @Test
    void updateAdminPassword_RegularAdminTriesToUpdateAnotherAdminSPassword_ResponseStatus403Forbidden() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setPassword(passwordEncoder.encode("updated"));
        String body = objectMapper.writeValueAsString(passwordDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        patch("/admins/" + secondRegularAdmin.getId() + "/password")
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
    void updateAdminPassword_RegularAdminUpdatesOwnPassword_ResponseStatus204NoContent() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setPassword(passwordEncoder.encode("updated"));
        String body = objectMapper.writeValueAsString(passwordDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/password")
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Admin> optionalAdmin = adminRepository.findById(firstRegularAdmin.getId());
        assertTrue(optionalAdmin.isPresent());
        assertEquals(optionalAdmin.get().getPassword(), passwordDTO.getPassword());
    }
    @Test
    void updateAdminPassword_SuperAdminUpdatesOtherPassword_ResponseStatus204NoContent() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setPassword(passwordEncoder.encode("updated"));
        String body = objectMapper.writeValueAsString(passwordDTO);
        // login as super admin
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
                        // update admin
                        patch("/admins/" + firstRegularAdmin.getId() + "/password")
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
        assertEquals(optionalAdmin.get().getPassword(), passwordDTO.getPassword());
    }

    @Test
    void updateAdminStatus_RegularAdminTriesToUpdateAnotherAdminSStatus_ResponseStatus403Forbidden() throws Exception {
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setStatus(Status.PENDING_CONFIRMATION);
        String body = objectMapper.writeValueAsString(statusDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        patch("/admins/" + secondRegularAdmin.getId() + "/status")
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
    void updateAdminStatus_RegularAdminTriesToUpdateOwnStatus_ResponseStatus204NoContent() throws Exception {
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setStatus(Status.PENDING_CONFIRMATION);
        String body = objectMapper.writeValueAsString(statusDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/status")
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Admin> optionalAdmin = adminRepository.findById(firstRegularAdmin.getId());
        assertTrue(optionalAdmin.isPresent());
        assertEquals(optionalAdmin.get().getStatus(), statusDTO.getStatus());
    }
    @Test
    void updateAdminStatus_SuperAdminUpdatesOtherStatus_ResponseStatus204NoContent() throws Exception {
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setStatus(Status.PENDING_CONFIRMATION);
        String body = objectMapper.writeValueAsString(statusDTO);
        // login as super admin
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
                        // update admin
                        patch("/admins/" + firstRegularAdmin.getId() + "/status")
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
        assertEquals(optionalAdmin.get().getStatus(), statusDTO.getStatus());
    }

    @Test
    void updateAdminShift_RegularAdminTriesToUpdateAnotherAdminSShift_ResponseStatus403Forbidden() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO();
        shiftDTO.setShift(Shift.EVENING);
        String body = objectMapper.writeValueAsString(shiftDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        patch("/admins/" + secondRegularAdmin.getId() + "/shift")
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
    void updateAdminShift_RegularAdminTriesToUpdateOwnShift_ResponseStatus403Forbidden() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO();
        shiftDTO.setShift(Shift.EVENING);
        String body = objectMapper.writeValueAsString(shiftDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/shift")
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
    void updateAdminShift_SuperAdminUpdatesOtherAdminShift_ResponseStatus204NoContent() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO();
        shiftDTO.setShift(Shift.EVENING);
        String body = objectMapper.writeValueAsString(shiftDTO);
        // login as super admin
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
                        // update admin
                        patch("/admins/" + firstRegularAdmin.getId() + "/shift")
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
        assertEquals(optionalAdmin.get().getShift(), shiftDTO.getShift());
    }
    @Test
    void updateAdminLocation_RegularAdminTriesToUpdateAnotherAdminSLocation_ResponseStatus403Forbidden() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocation("UPDATED LOCATION");
        String body = objectMapper.writeValueAsString(locationDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        patch("/admins/" + secondRegularAdmin.getId() + "/location")
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
    void updateAdminLocation_RegularAdminUpdatesOwnLocation_ResponseStatus204NoContent() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocation("UPDATED LOCATION");
        String body = objectMapper.writeValueAsString(locationDTO);
        // login as admin1
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        patch("/admins/" + firstRegularAdmin.getId() + "/location")
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Admin> optionalAdmin = adminRepository.findById(firstRegularAdmin.getId());
        assertTrue(optionalAdmin.isPresent());
        assertEquals(optionalAdmin.get().getLocation(), locationDTO.getLocation());
    }
    @Test
    void updateAdminLocation_SuperAdminUpdatesOtherAdminLocation_ResponseStatus204NoContent() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocation("UPDATED LOCATION");
        String body = objectMapper.writeValueAsString(locationDTO);
        // login as super admin
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
                        // update admin
                        patch("/admins/" + firstRegularAdmin.getId() + "/location")
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
        assertEquals(optionalAdmin.get().getLocation(), locationDTO.getLocation());
    }

    @Test
    void delete_RegularAdminTriesToDeleteAnotherAdmin_ResponseStatus403Forbidden() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        delete("/admins/" + secondRegularAdmin.getId())
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void delete_RegularAdminTriesToDeleteOwnAdmin_ResponseStatus403Forbidden() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(firstRegularAdminUsername, firstRegularAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        delete("/admins/" + firstRegularAdmin.getId())
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    void delete_SuperAdminToDeletesAdmin_ResponseStatus204NoContent() throws Exception {
        HttpHeaders httpHeaders = getJWTLoginHeaders(superAdminUsername, superAdminPassword);
        mvcResult = mockMvc.perform(
                        // try to update admin2
                        delete("/admins/" + firstRegularAdmin.getId())
                                .headers(httpHeaders)
                                .param("username", firstRegularAdmin.getUsername())
                                .param("password", firstRegularAdmin.getPassword())
                )
                .andExpect(status().isNoContent())
                .andReturn();
        assertFalse(adminRepository.existsById(firstRegularAdmin.getId()));
    }
    private HttpHeaders getJWTLoginHeaders(String username, String password) throws Exception {
        mvcResult = mockMvc.perform(get("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        String token = jsonObject.getString("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        return httpHeaders;
    }

}