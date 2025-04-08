package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "chat_participants")
public class ChatParticipant implements Serializable {

    private static final long serialVersionUID = 9L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one relationship: many ChatParticipant records belong to one ChatChannel.
    // The @JoinColumn annotation specifies the column (channel_id) in the chat_participants table that holds the foreign key.
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChatChannel channel;

    // Many-to-one relationship: many ChatParticipant records belong to one User.
    // The @JoinColumn annotation specifies the foreign key column (user_id) in the table.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // "admin" or "member"
    private String role;

    // Default constructor is required by JPA.
    public ChatParticipant() {
    }

    // Parameterized constructor for easy instantiation with a user and role.
    public ChatParticipant(User user, String role) {
        this.user = user;
        this.role = role;
    }

    // Getter for id.
    public Long getId() {
        return id;
    }

    // Setter for id.
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for channel.
    public ChatChannel getChannel() {
        return channel;
    }

    // Setter for channel.
    public void setChannel(ChatChannel channel) {
        this.channel = channel;
    }

    // Getter for user.
    public User getUser() {
        return user;
    }

    // Setter for user.
    public void setUser(User user) {
        this.user = user;
    }

    // Getter for role.
    public String getRole() {
        return role;
    }

    // Setter for role.
    public void setRole(String role) {
        this.role = role;
    }
}

