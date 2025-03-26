import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;

public class MatchGetDTO {
    private Long id;           // Unique identifier for the match
    private Long userId1;      // First user in the match
    private Long userId2;      // Second user in the match
    private MatchStatus status; // Current match status (e.g., PENDING, CONFIRMED)

    public MatchGetDTO(Long id, Long userId1, Long userId2, MatchStatus status) {
        this.id = id;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }
}
