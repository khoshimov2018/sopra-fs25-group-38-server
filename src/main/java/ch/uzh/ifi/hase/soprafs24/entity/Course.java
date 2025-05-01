package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COURSE")
public class Course implements Serializable {

    private static final long serialVersionUID = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

       
    public Course(Long id, String courseName) {
        this.id = id;
        this.courseName = courseName;
    }

    @Column(nullable = false, unique = true)
    private String courseName;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCourse> userCourses = new ArrayList<>();


    
    public Course() {}

    
    public Course(String courseName) {
        this.courseName = courseName;
    }

    
    public Long getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<UserCourse> getUserCourses() {
        return userCourses;
    }
    
    public void setUserCourses(List<UserCourse> userCourses) {
        this.userCourses = userCourses;
    }
    
}
