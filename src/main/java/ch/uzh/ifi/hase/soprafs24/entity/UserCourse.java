package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_course")
public class UserCourse implements Serializable {

    private static final long serialVersionUID = 6L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileKnowledgeLevel knowledgeLevel;

    public UserCourse() {}
    
    public UserCourse(User user, Course course, ProfileKnowledgeLevel knowledgeLevel) {
        this.user = user;
        this.course = course;
        this.knowledgeLevel = knowledgeLevel;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ProfileKnowledgeLevel getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(ProfileKnowledgeLevel knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }
    
}
