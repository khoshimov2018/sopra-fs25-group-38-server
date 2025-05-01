package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.repository.NotificationRepository;
import ch.uzh.ifi.hase.soprafs24.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;

    @BeforeEach
    public void setup() {
        notificationRepository.deleteAll();
        userId = 1L;

        Notification n1 = new Notification(userId, "Match found!", null, null);
        Notification n2 = new Notification(userId, "New message received", null, null);
        notificationRepository.saveAll(List.of(n1, n2));
    }

    @Test
    public void getNotificationsForUser_returnsAll() throws Exception {
        mockMvc.perform(get("/notifications/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getUnreadNotificationsForUser_returnsUnreadOnly() throws Exception {
        mockMvc.perform(get("/notifications/user/" + userId + "/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void markNotificationAsRead_marksSuccessfully() throws Exception {
        Notification notification = notificationRepository.findAll().get(0);

        mockMvc.perform(put("/notifications/" + notification.getId() + "/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    public void markAllNotificationsAsRead_works() throws Exception {
        mockMvc.perform(put("/notifications/user/" + userId + "/read-all"))
                .andExpect(status().isOk());

        List<Notification> updated = notificationRepository.findAll();
        assert updated.stream().allMatch(Notification::isRead);
    }

    @Test
    public void deleteNotification_removesEntry() throws Exception {
        Notification notification = notificationRepository.findAll().get(0);

        mockMvc.perform(delete("/notifications/" + notification.getId()))
                .andExpect(status().isOk());
    }
}
