package ch.uzh.ifi.hase.soprafs24.rest.dto;

// used to return information of a channel

import java.util.List;

public class ChatChannelGetDTO {
    private Long channelId;
    private String channelName;
    private String channelType; // 'individual' or 'group'
    private String channelProfileImage;
    private String createdAt;
    private String updatedAt;
    private List<ChatParticipantGetDTO> participants;

    // getter and setter

    public Long getChannelId() {
        return channelId;
    }
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    public String getChannelType() {
        return channelType;
    }
    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
    public String getChannelProfileImage() {
        return channelProfileImage;
    }
    public void setChannelProfileImage(String channelProfileImage) {
        this.channelProfileImage = channelProfileImage;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public List<ChatParticipantGetDTO> getParticipants() {
        return participants;
    }
    public void setParticipants(List<ChatParticipantGetDTO> participants) {
        this.participants = participants;
    }
}
