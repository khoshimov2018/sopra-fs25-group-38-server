package ch.uzh.ifi.hase.soprafs24.rest.dto;

// used when recieves a message from frontend

public class MessagePostDTO {
    private Long senderId;
    private String context;

    // getter and setter
    
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
}
