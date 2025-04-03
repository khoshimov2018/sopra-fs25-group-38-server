package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.
import java.io.Serializable; 
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;

@Entity
@Table(name = "PROFILE")
public class Profile implements Serializable{
    
    private static final long serialVersionUID = 7L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true)
    private ProfileKnowledgeLevel knowledgeLevel;

    @Column(nullable = true)
    private String studyGoals;

    @Column(nullable = true)
    private String bio;

    
}
