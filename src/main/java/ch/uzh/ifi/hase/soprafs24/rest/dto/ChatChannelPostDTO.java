package ch.uzh.ifi.hase.soprafs24.rest.dto;

// used to create a chat channel (recieve information from client)

import java.util.List;

public class ChatChannelPostDTO {
    private String channelName;
    private String channelType; // "individual" or "group"
    private List<Long> participantIds; 
    private String channelProfileImage;

    // Getters and setters
    
    public List<Long> getParticipantIds() {
        return participantIds;
    }
    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
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
}
