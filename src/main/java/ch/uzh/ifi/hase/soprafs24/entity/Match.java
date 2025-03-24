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
    private Long idUser1;

    @Column(nullable = false)
    private Long idUser2;

    @Column(nullable = false)
    private MatchStatus status;

    /* 
       get & set method
    */ 
    
    // id
    public Long getId() {
        return id;
    }

    // user1's id
    public Long getUser1Id() {
        return this.idUser1;
    }

    public void setUser1Id(Long idUser1) {
        this.idUser1 = idUser1;
    }

    // user2's id
    public Long getUser2Id() {
        return this.idUser2;
    }

    public void setUser2Id(Long idUser2) {
        this.idUser2 = idUser2;
    }

    // status
    public MatchStatus getStatus() {
        return this.status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    /*
     * constructor
     */

     public Match(){
     }

     public Match(User user1, User user2, MatchStatus status){
        this.idUser1 = user1.getId();
        this.idUser2 = user2.getId();
        this.status = status;
     }
}
