package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class CreateChannelDTO {
    private String name;
    private String type; // "individual" or "group"
    private List<Long> participantIds; 

    // Getters and setters
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<Long> getParticipantIds() {
        return participantIds;
    }
    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }
}
