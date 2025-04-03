package ch.uzh.ifi.hase.soprafs24.rest.dto;


public class CourseGetDTO {
    private Long id;
    private Long userId;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}