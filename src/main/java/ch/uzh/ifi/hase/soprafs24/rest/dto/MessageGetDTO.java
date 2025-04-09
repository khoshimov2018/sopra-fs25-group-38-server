package ch.uzh.ifi.hase.soprafs24.rest.dto;

// used to return the message details back to frontend

public class MessageGetDTO {
    private Long messageId;
    private Long senderId;
    private Long channelId; // new added
    private String senderProfileImage;
    private String context;
    private String timestamp;

    // getter and setter 

    public Long getMessageId() {
        return messageId;
    }
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public Long getChannelId() {
        return channelId;
    }
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
    public String getSenderProfileImage() {
        return senderProfileImage;
    }
    public void setSenderProfileImage(String senderProfileImage) {
        this.senderProfileImage = senderProfileImage;
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
