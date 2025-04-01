package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.
import java.io.Serializable;
import java.time.LocalDate; 

@Entity
@Table(name = "COURSE")
public class Course implements Serializable{
    
    private static final long serialVersionUID = 5L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String courseName;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCourseName() {
        return courseName;
    }

    /*
     * constructor
     */
    public Course() {
    }

    public Course(User user, String courseName) {
        this.userId = user.getId();
        this.courseName = courseName;
    }
}
