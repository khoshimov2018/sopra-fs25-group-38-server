package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserTypingStatusPushDTO {
    private Long userId;
    private boolean typing;

    public UserTypingStatusPushDTO() {}

    public UserTypingStatusPushDTO(Long userId, boolean typing) {
        this.userId = userId;
        this.typing = typing;
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
}
