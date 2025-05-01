package ch.uzh.ifi.hase.soprafs24.entity.chat;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.entity.ChatParticipant; 

@Entity
@Table(name = "channel")
public class ChatChannel implements Serializable{

    private static final long serialVersionUID = 10L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional 
    private String name;

    // Indicates the type ("individual" or "group")
    private String type;

    private String channelProfileImage;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatChannel() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ChatChannel(String name, String type, String channelProfileImage) {
        this();
        this.name = name;
        this.type = type;
        this.channelProfileImage = channelProfileImage;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public String getChannelProfileImage() {
        return channelProfileImage;
    }
    public void setChannelProfileImage(String channelProfileImage) {
        this.channelProfileImage = channelProfileImage;
    }
    public List<ChatParticipant> getParticipants() {
        return participants;
    }
    public void setParticipants(List<ChatParticipant> participants) {
        this.participants = participants;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // method to add a participant.
    public void addParticipant(ChatParticipant participant) {
        participants.add(participant);
        participant.setChannel(this); // Sets the bidirectional link.
    }

    // method to remove a participant.
    public void removeParticipant(ChatParticipant participant) {
        participants.remove(participant);
        participant.setChannel(null);
    }

}
