package ch.uzh.ifi.hase.soprafs24.rest.dto;
import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;


public class MatchPostDTO {
    private Long userId;       // ID of the user performing the action
    private Long targetUserId; // ID of the user being liked/disliked

    private MatchStatus status;
    private boolean likedByUser1;
    private boolean likedByUser2;

    public MatchPostDTO() {
        this.likedByUser1 = false;
        this.likedByUser2 = false;
    }

    public MatchPostDTO(Long userId, Long targetUserId) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.likedByUser1 = false;
        this.likedByUser2 = false;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
    public MatchStatus getStatus() {
        return status;
    }
    public void setStatus(MatchStatus status) {
        this.status = status;
    }
    public boolean isLikedByUser1() {
        return likedByUser1;
    }
    public void setLikedByUser1(boolean likedByUser1) {
        this.likedByUser1 = likedByUser1;
    }
    public boolean isLikedByUser2() {
        return likedByUser2;
    }
    public void setLikedByUser2(boolean likedByUser2) {
        this.likedByUser2 = likedByUser2;
    }
}
