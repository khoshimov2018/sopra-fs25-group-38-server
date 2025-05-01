package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.config.DataInitializer;
import ch.uzh.ifi.hase.soprafs24.config.TestSecurityConfig;
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.UserCourse;
import ch.uzh.ifi.hase.soprafs24.entity.Match;
import ch.uzh.ifi.hase.soprafs24.entity.Block;
import ch.uzh.ifi.hase.soprafs24.entity.Report;

import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ReportDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.BlockDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.repository.BlockRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatChannelRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ReportRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserCourseRepository;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserDeletionIntegrationTest
 *
 * This is an integration test verifying the complete deletion flow of a user account,
 * ensuring that all related entities such as matches, chat channels, blocks, reports,
 * course selections, and user profile are removed.
 *
 * Focuses on the protocol-level behavior including HTTP status codes and proper cascading deletes.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import({TestSecurityConfig.class, DataInitializer.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDeletionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatChannelRepository chatChannelRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private ReportRepository reportRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Integration Test: Complete User Deletion Flow
     * 
     * Simulates the full lifecycle of a user from registration to deletion, verifying that:
     * - All associated data including matches, chat channels, blocks, reports, and course selections
     *   are properly deleted.
     *
     * Steps:
     * 1. Register the target user
     * 2. Register two sample users
     * 3. Create mutual match between target user and sample user
     * 4. Create individual and group chat channels involving the target user
     * 5. Create block relations (target blocks someone and is blocked by someone)
     * 6. Create report relations (target reports someone and is reported by someone)
     * 7. Delete the target user using DELETE /users/me
     * 8. Verify 204 NO_CONTENT status is returned
     * 9. Assert deletion of:
     *    - the user entity,
     *    - related chat participation and channels,
     *    - all match records involving the user,
     *    - all user course selections,
     *    - all block relations involving the user,
     *    - all reports where the user is reporter or reported
     */

        @Test
        void testUserDeletionFlow() throws Exception {

        CourseSelectionDTO selection1 = new CourseSelectionDTO();
        selection1.setCourseId(1L); // AI
        selection1.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER);
        
        CourseSelectionDTO selection2 = new CourseSelectionDTO();
        selection2.setCourseId(3L); // Software Engineering
        selection2.setKnowledgeLevel(ProfileKnowledgeLevel.INTERMEDIATE);
        
        // 1. Register the target user
        UserPostDTO targetuser = new UserPostDTO();
        targetuser.setName("Target User");
        targetuser.setEmail("target-user@example.com");
        targetuser.setPassword("password123");
        targetuser.setStudyLevel("Bachelor");
        targetuser.setStudyGoals(List.of("project work"));
        targetuser.setAvailability(UserAvailability.EVENING);
        targetuser.setCourseSelections((List.of(selection1)));

        // 2. Register two more sample users
        UserPostDTO sampleuser1 = new UserPostDTO();
        sampleuser1.setName("Sample User1");
        sampleuser1.setEmail("sample-user1@example.com");
        sampleuser1.setPassword("password123");
        sampleuser1.setStudyLevel("Bachelor");
        sampleuser1.setStudyGoals(List.of("project work"));
        sampleuser1.setAvailability(UserAvailability.EVENING);
        sampleuser1.setCourseSelections((List.of(selection1)));
        
        UserPostDTO sampleuser2 = new UserPostDTO();
        sampleuser2.setName("Sample User2");
        sampleuser2.setEmail("sample-user2@example.com");
        sampleuser2.setPassword("password123");
        sampleuser2.setStudyLevel("Bachelor");
        sampleuser2.setStudyGoals(List.of("project work"));
        sampleuser2.setAvailability(UserAvailability.EVENING);
        sampleuser2.setCourseSelections((List.of(selection1, selection2)));

        // Register the user
        Map<String, Object> targetMap = registerUser(targetuser);
        Long targetId = Long.valueOf((Integer) targetMap.get("id"));
        String targetToken = (String) targetMap.get("token");

        Map<String, Object> sample1Map = registerUser(sampleuser1);
        Long sample1Id = Long.valueOf((Integer) sample1Map.get("id"));
        String sample1Token = (String) sample1Map.get("token");


        Map<String, Object> sample2Map = registerUser(sampleuser2);
        Long sample2Id = Long.valueOf((Integer) sample2Map.get("id"));
        String sample2Token = (String) sample2Map.get("token");

        // 3. Create mutual match between target user and sample user1
        // 3-1. sample1Id likes targetId
        MatchPostDTO like1 = new MatchPostDTO(sample1Id, targetId);
        mockMvc.perform(post("/matches/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(like1)))
                .andExpect(status().isCreated());

        // 3-2. targetId likes sample1Id(matched)
        MatchPostDTO like2 = new MatchPostDTO(targetId, sample1Id);
        mockMvc.perform(post("/matches/like")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + targetToken)
                .content(asJsonString(like2)))
                .andExpect(status().isCreated());

        // 4. Create chat channels involving the target user
        // 4-1. Create an individual chat between target and sample user
        ChatChannelPostDTO individualChat = new ChatChannelPostDTO();
        individualChat.setChannelType("individual");
        individualChat.setChannelProfileImage("group.png");
        individualChat.setParticipantIds(Arrays.asList(targetId, sample1Id));

        String individualPostContent = objectMapper.writeValueAsString(individualChat);

        mockMvc.perform(post("/chat/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(individualPostContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channelId").exists())
                .andExpect(jsonPath("$.channelType", is("individual")))
                .andExpect(jsonPath("$.channelProfileImage", is("group.png")))
                .andExpect(jsonPath("$.participants", hasSize(2)));

        // 4-2. Create a group chat including all three users
        ChatChannelPostDTO groupChat = new ChatChannelPostDTO();
        groupChat.setChannelName("Study Group");
        groupChat.setChannelType("group");
        groupChat.setChannelProfileImage("group.png");
        groupChat.setParticipantIds(Arrays.asList(targetId, sample1Id, sample2Id));

        String groupPostContent = objectMapper.writeValueAsString(groupChat);

        mockMvc.perform(post("/chat/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupPostContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channelId").exists())
                .andExpect(jsonPath("$.channelName", is("Study Group")))
                .andExpect(jsonPath("$.channelType", is("group")))
                .andExpect(jsonPath("$.channelProfileImage", is("group.png")))
                .andExpect(jsonPath("$.participants", hasSize(3)));

        // 5. Create block relations
        // 5-1. Target user blocks sampleUser1
        BlockDTO block1 = new BlockDTO();
        block1.setBlockerId(targetId);
        block1.setBlockedUserId(sample1Id);

        Map<String, Long> blockPayload1 = Map.of(
                "blockerId", targetId,
                "blockedUserId", sample1Id
        );
        
        mockMvc.perform(post("/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blockPayload1)))
                .andExpect(status().isCreated());
        


        // 5-2. Target user is blocked by sampleUser2
        BlockDTO block2 = new BlockDTO();
        block2.setBlockerId(sample2Id);
        block2.setBlockedUserId(targetId);

        Map<String, Long> blockPayload2 = Map.of(
                "blockerId", sample2Id,
                "blockedUserId", targetId
        );
        
        mockMvc.perform(post("/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blockPayload2)))
                .andExpect(status().isCreated());
        

        // 6. Create report relations
        // 6-1. Target user reports sampleUser1
        ReportDTO report1 = new ReportDTO();
        report1.setReporterId(targetId);
        report1.setReportedId(sample1Id);
        report1.setReason("Inappropriate behavior");
    
        mockMvc.perform(post("/reports")
                .header("Authorization", "Bearer " + targetToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(report1)))
            .andExpect(status().isCreated());
    
        // 6-2. Target user is reported by sampleUser2
        ReportDTO report2 = new ReportDTO();
        report2.setReporterId(sample2Id);
        report2.setReportedId(targetId);
        report2.setReason("Inappropriate behavior");
    
        mockMvc.perform(post("/reports")
                .header("Authorization", "Bearer " + sample2Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(report2)))
            .andExpect(status().isCreated());
    
        // 7. Delete the target user account using DELETE /users/me
        mockMvc.perform(delete("/users/me")
        .header("Authorization", "Bearer " + targetToken))
        .andExpect(status().isNoContent());

        // 8. Verify 404 Not Found status (user no longer exists)
        mockMvc.perform(get("/users/" + targetId)
        .header("Authorization", "Bearer " + sample1Token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        // 9. Verify deletion results
        // 9-1. Chat channels involving target user should no longer be accessible
        mockMvc.perform(get("/chat/channels/user/" + targetId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
        
        // 9-2. Matches involving target user should be removed
        List<Match> matches = matchRepository.findAll();
        assertTrue(matches.stream().noneMatch(m ->
                m.getUserId1().equals(targetId) || m.getUserId2().equals(targetId)),
                "Deleted user's matches should be removed");

        // 9-3. Course selections (UserCourse) by target user should be removed
        List<UserCourse> courses = userCourseRepository.findAll();
        assertFalse(courses.stream().anyMatch(c ->
                c.getUser().getId().equals(targetId)),
                "Deleted user's course selections should also be removed");
    
        // 9-4. Block relations involving target user should be removed
        List<Block> blocks = blockRepository.findAll();
        assertTrue(blocks.stream().noneMatch(b ->
                b.getBlockerId().equals(targetId) || b.getBlockedUserId().equals(targetId)),
                "Deleted user's blocks should be removed");

        // 9-5. Reports involving target user should be removed
        List<Report> reports = reportRepository.findAll();
        assertTrue(reports.stream().noneMatch(r ->
                r.getReporterId().equals(targetId) || r.getreportedUserId().equals(targetId)),
                "Deleted user's reports should be removed");
}

    /**
     * Helper Method to register users
     */
    private Map<String, Object> registerUser(UserPostDTO dto) throws Exception {
            MvcResult result = mockMvc.perform(post("/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            return objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
            }

    /**
     * Helper Method to convert objects into a JSON string
     */
     private String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }  
} 