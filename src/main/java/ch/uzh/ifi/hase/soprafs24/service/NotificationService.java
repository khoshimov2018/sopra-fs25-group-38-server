package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.NotificationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private static final String USER_NOT_FOUND_PREFIX = "User with ID ";
    private static final String NOT_FOUND_SUFFIX = " not found.";

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotification(Long userId, String message, String type, Long relatedEntityId) {
        ensureUserExists(userId);
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
        ensureUserExists(userId);
        return notificationRepository.findByUserIdOrderByCreationDateDesc(userId);
    }

    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        ensureUserExists(userId);
        return notificationRepository.findByUserIdAndReadFalseOrderByCreationDateDesc(userId);
    }

    public Notification markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notification with ID " + notificationId + NOT_FOUND_SUFFIX));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllNotificationsAsRead(Long userId) {
        ensureUserExists(userId);
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreationDateDesc(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public void deleteNotification(Long notificationId) {
        if (notificationRepository.findById(notificationId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Notification with ID " + notificationId + NOT_FOUND_SUFFIX);
        }
        notificationRepository.deleteById(notificationId);
    }

    public void createLikeNotification(Long targetUserId, Long likingUserId) {
        User likingUser = userRepository.findById(likingUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        USER_NOT_FOUND_PREFIX + likingUserId + NOT_FOUND_SUFFIX));
        String message = likingUser.getName() + " liked your profile!";
        createNotification(targetUserId, message, "LIKE", likingUserId);
    }

    public void createMatchNotification(Long userId1, Long userId2, Long matchId) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        USER_NOT_FOUND_PREFIX + userId1 + NOT_FOUND_SUFFIX));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        USER_NOT_FOUND_PREFIX + userId2 + NOT_FOUND_SUFFIX));

        createNotification(userId1, "You matched with " + user2.getName() + "!", "MATCH", matchId);
        createNotification(userId2, "You matched with " + user1.getName() + "!", "MATCH", matchId);
    }

    private void ensureUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    USER_NOT_FOUND_PREFIX + userId + NOT_FOUND_SUFFIX);
        }
    }
}
