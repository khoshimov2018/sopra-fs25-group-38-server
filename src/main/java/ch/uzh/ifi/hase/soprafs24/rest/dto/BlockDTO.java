package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class BlockDTO {
    private Long blockerId;
    private Long blockedUserId;

    public BlockDTO() {}

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

