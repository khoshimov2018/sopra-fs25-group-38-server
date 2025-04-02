package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.

import java.io.Serializable; 
import java.time.LocalDate; 

@Entity
@Table(name = "STUDYPLAN")
public class StudyPlan implements Serializable{
    
    private static final long serialVersionUID = 6L;
    
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate generatedDate;

    @Column(nullable = false)
    private String planContent;

    /*
     * getter and setter
     */
    public Long getUserId() {
        return userId;
    }

    public LocalDate generatedDate() {
        return generatedDate;
    }

    public void setGeneratedDate() {
        this.generatedDate = LocalDate.now();
    }

    public String getPlanContent() {
        return planContent;
    }

    public void setPlanContent(String planContent) {
        this.planContent = planContent;
    }


    public StudyPlan() {
        this.generatedDate = LocalDate.now();
    }

    public StudyPlan(User user, String planContent) {
        this.generatedDate = LocalDate.now();
        this.userId = user.getId();
        this.planContent = planContent;
    }

}
