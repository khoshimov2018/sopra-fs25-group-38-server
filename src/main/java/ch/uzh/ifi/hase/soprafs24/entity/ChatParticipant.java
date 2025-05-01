package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.*;

import ch.uzh.ifi.hase.soprafs24.entity.chat.ChatChannel;

@Entity
@Table(name = "chat_participants")
public class ChatParticipant implements Serializable {

    private static final long serialVersionUID = 9L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChatChannel channel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // "admin" or "member"
    private String role;

    public ChatParticipant() {
    }

    public ChatParticipant(User user, String role) {
        this.user = user;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatChannel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

