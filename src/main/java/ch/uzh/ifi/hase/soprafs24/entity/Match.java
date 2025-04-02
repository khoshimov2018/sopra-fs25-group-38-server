package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.
import java.io.Serializable; 
import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;

@Entity
@Table(name = "MATCH")
public class Match implements Serializable{ 

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId1;

    @Column(nullable = false)
    private Long userId2;

    @Column(nullable = false)
    private MatchStatus status;

    @Column(nullable = false)
    private boolean likedByUser1;

    @Column(nullable = false)
    private boolean likedByUser2;


    /* 
       get & set method
    */ 
    
    // id
    public Long getId() {
        return id;
    }

    // user1's id
    public Long getUserId1() {
        return this.userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    // user2's id
    public Long getUserId2() {
        return this.userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    // status
    public MatchStatus getStatus() {
        return this.status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public boolean isLikedByUser1() {
        return likedByUser1;
    }
    
    public void setLikedByUser1(boolean likedByUser1) {
        this.likedByUser1 = likedByUser1;
    }
    
    public boolean isLikedByUser2() {
        return likedByUser2;
    }
    
    public void setLikedByUser2(boolean likedByUser2) {
        this.likedByUser2 = likedByUser2;
    }
    /*
     * constructor
     */

     public Match(){
     }

     public Match(User user1, User user2, MatchStatus status, boolean likedByUser1, boolean likedByUser2) {
        this.userId1 = user1.getId();
        this.userId2 = user2.getId();
        this.status = status;
        this.likedByUser1 = likedByUser1;
        this.likedByUser2 = likedByUser2;
    }
}
