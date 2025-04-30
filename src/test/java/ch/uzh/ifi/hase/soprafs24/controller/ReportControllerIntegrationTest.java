package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.config.TestSecurityConfig;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ReportDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Long reporterId;
    private Long reportedId;

    @BeforeEach
    public void setup() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        reporterId = registerTestUser("reporter@example.com");
        reportedId = registerTestUser("reported@example.com");
    }

    @Test
    public void testReportUserSuccessfully() throws Exception {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReporterId(reporterId);
        reportDTO.setReportedId(reportedId);
        reportDTO.setReason("Spamming inappropriate content");

        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportDTO)))
            .andExpect(status().isCreated());
    }

    @Test
    public void testAdminCanViewReports() throws Exception {
        // First report a user
        testReportUserSuccessfully();

        // Login using the seeded admin account
        String adminToken = loginAs("admin@example.com", "securePassword123");

        mockMvc.perform(get("/reports")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].reason").value("Spamming inappropriate content"));
    }

    private Long registerTestUser(String email) throws Exception {
        return registerTestUser(email, "password123");
    }

    private Long registerTestUser(String email, String password) throws Exception {
        var user = Map.of(
                "name", "User",
                "email", email,
                "password", password,
                "studyLevel", "Bachelor",
                "studyGoals", List.of("exam prep")
        );

        var result = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andReturn();

        Map<String, Object> response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        return Long.valueOf((Integer) response.get("id"));
    }


    private String loginAs(String email, String password) throws Exception {
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Login response body was empty");
        }

        UserGetDTO user = objectMapper.readValue(content, UserGetDTO.class);
        return user.getToken();
    }
}
