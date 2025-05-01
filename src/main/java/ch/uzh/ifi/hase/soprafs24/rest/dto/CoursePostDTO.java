package ch.uzh.ifi.hase.soprafs24.rest.dto;
import java.util.List;

public class CoursePostDTO {
    private List<Long> courseIds;
    private String courseName;

    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = courseIds;
    }
    
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
