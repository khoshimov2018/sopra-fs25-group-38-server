package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.rest.dto.NotificationGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationGetDTO>> getNotificationsForUser(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        List<NotificationGetDTO> notificationGetDTOs = new ArrayList<>();

        for (Notification notification : notifications) {
            notificationGetDTOs.add(DTOMapper.INSTANCE.convertEntityToNotificationGetDTO(notification));
        }

        return ResponseEntity.ok(notificationGetDTOs);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationGetDTO>> getUnreadNotificationsForUser(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        List<NotificationGetDTO> notificationGetDTOs = new ArrayList<>();

        for (Notification notification : notifications) {
            notificationGetDTOs.add(DTOMapper.INSTANCE.convertEntityToNotificationGetDTO(notification));
        }

        return ResponseEntity.ok(notificationGetDTOs);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationGetDTO> markNotificationAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationService.markNotificationAsRead(notificationId);
        NotificationGetDTO notificationGetDTO = DTOMapper.INSTANCE.convertEntityToNotificationGetDTO(notification);

        return ResponseEntity.ok(notificationGetDTO);
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable Long userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}