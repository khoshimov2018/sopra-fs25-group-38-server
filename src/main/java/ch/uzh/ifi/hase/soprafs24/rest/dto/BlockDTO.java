package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class BlockDTO {
    private Long blockerId;
    private Long blockedUserId;

    /**
     * Default constructor is required for frameworks like Jackson
     * that use reflection to instantiate DTOs during deserialization.
     * This constructor intentionally contains no logic.
     */
    public BlockDTO() {
        // Intentionally left blank
    }

    public Long getBlockerId() {
        return blockerId;
    }

    public void setBlockerId(Long blockerId) {
        this.blockerId = blockerId;
    }

    public Long getBlockedUserId() {
        return blockedUserId;
    }

    public void setBlockedUserId(Long blockedUserId) {
        this.blockedUserId = blockedUserId;
    }
}

