package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class SendMessageDTO {

    private Long senderId;
    private String content;

    // Getters and setters
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
