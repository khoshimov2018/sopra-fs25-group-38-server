package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "channel")
public class ChatChannel {

    // @GeneratedValue with strategy=GenerationType.IDENTITY tells JPA that the database generates the unique ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional 
    private String name;

    // Indicates the type ("individual" or "group")
    private String type;

    // One channel can have many participants.
    // mappedBy = "channel" means the "channel" field in ChatParticipant owns the relationship. the ChatParticipant entity has a field called channel that holds the foreign key.
    // cascade = CascadeType.ALL ensures that any changes to ChatChannel cascade to its child entity - ChatParticipant.
    // Orphan removal ensures that if a ChatParticipant is removed from the participants list in ChatChannel, JPA will delete that participant row from the database.
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    // Timestamps for creation and last update.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor: sets creation and update timestamps.
    public ChatChannel() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Parameterized constructor to set name and type.
    public ChatChannel(String name, String type) {
        this();
        this.name = name;
        this.type = type;
    }

    // Getters and setters for each field.
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

    // Helper method to add a participant.
    public void addParticipant(ChatParticipant participant) {
        participants.add(participant);
        participant.setChannel(this); // Sets the bidirectional link.
    }

    // Helper method to remove a participant.
    public void removeParticipant(ChatParticipant participant) {
        participants.remove(participant);
        participant.setChannel(null);
    }
}
