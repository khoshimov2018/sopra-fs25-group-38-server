package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "userc_typing_status")
public class UserTypingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Association with User.
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    
    // Typing flag: true if the user is currently typing.
    @Column(nullable = false)
    private boolean typing;

    public UserTypingStatus() {
    }

    public UserTypingStatus(User user, boolean typing) {
        this.user = user;
        this.typing = typing;
    }

    // Getters and setters.
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}

