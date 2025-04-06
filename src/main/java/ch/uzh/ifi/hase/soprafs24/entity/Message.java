package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

import org.apache.tomcat.jni.Local;

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

    // getter and setter
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public ChatChannel getChannel(){
        return channel;
    }
    public void setChannel(ChatChannel channel){
        this.channel = channel;
    }
    public User getSender(){
        return sender;
    }
    public void setSender(User sender){
        this.sender = sender;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
    public LocalDateTime getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

}

