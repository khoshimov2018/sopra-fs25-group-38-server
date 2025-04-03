package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the channel where the message is posted
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChatChannel channel;

    // Sender of the message
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false)
    private String content;

    private LocalDateTime timestamp;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(ChatChannel channel, User sender, String content) {
        this();
        this.channel = channel;
        this.sender = sender;
        this.content = content;
    }

}

