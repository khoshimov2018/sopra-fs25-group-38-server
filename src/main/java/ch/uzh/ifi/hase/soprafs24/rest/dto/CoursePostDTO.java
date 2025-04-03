package ch.uzh.ifi.hase.soprafs24.rest.dto;
import java.util.List;

public class CoursePostDTO {
    private List<Long> courseIds;

    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = courseIds;
    }
    
}
