package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserTypingStatusGetDTO {
    private Long userId;
    private boolean typing;
    private UserStatus userStatus; // ONLINE or OFFLINE from the User entity

    public UserTypingStatusGetDTO() {}

    public UserTypingStatusGetDTO(Long userId, boolean typing, UserStatus userStatus) {
        this.userId = userId;
        this.typing = typing;
        this.userStatus = userStatus;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
