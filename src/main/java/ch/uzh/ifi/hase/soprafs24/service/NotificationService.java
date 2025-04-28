package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.NotificationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.NotificationGetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotification(Long userId, String message, String type, Long relatedEntityId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with ID " + userId + " not found."));

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setCreationDate(LocalDateTime.now());
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with ID " + userId + " not found."));

        return notificationRepository.findByUserIdOrderByCreationDateDesc(userId);
    }

    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with ID " + userId + " not found."));

        return notificationRepository.findByUserIdAndReadFalseOrderByCreationDateDesc(userId);
    }

    public Notification markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);

        if (notificationOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Notification with ID " + notificationId + " not found.");
        }

        Notification notification = notificationOptional.get();
        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    public void markAllNotificationsAsRead(Long userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with ID " + userId + " not found."));

        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreationDateDesc(userId);
        
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public void deleteNotification(Long notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);

        if (notificationOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Notification with ID " + notificationId + " not found.");
        }

        notificationRepository.deleteById(notificationId);
    }

    public void createLikeNotification(Long targetUserId, Long likingUserId) {
        User likingUser = userRepository.findById(likingUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with ID " + likingUserId + " not found."));

        String message = likingUser.getName() + " liked your profile!";
        createNotification(targetUserId, message, "LIKE", likingUserId);
    }

    public void createMatchNotification(Long userId1, Long userId2, Long matchId) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with ID " + userId1 + " not found."));

        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with ID " + userId2 + " not found."));

        // Create notification for user 1
        String message1 = "You matched with " + user2.getName() + "!";
        createNotification(userId1, message1, "MATCH", matchId);

        // Create notification for user 2
        String message2 = "You matched with " + user1.getName() + "!";
        createNotification(userId2, message2, "MATCH", matchId);
    }
}