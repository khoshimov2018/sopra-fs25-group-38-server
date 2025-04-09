package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.

import java.io.Serializable; 
import java.time.LocalDate; 

@Entity
@Table(name = "BLOCK")
public class Block implements Serializable {

    private static final long serialVersionUID = 4L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long blockerId;

    @Column(nullable = false)
    private Long blockedUserId;

    @Column(nullable = false)
    private LocalDate blockDate;

    // Constructors
    public Block() {
        this.blockDate = LocalDate.now();
    }

    public Block(User blocker, User blockedUser) {
        this.blockerId = blocker.getId();
        this.blockedUserId = blockedUser.getId();
        this.blockDate = LocalDate.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getBlockerId() {
        return blockerId;
    }

    public Long getBlockedUserId() {
        return blockedUserId;
    }

    public LocalDate getBlockDate() {
        return blockDate;
    }
}
