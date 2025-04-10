package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;

public class CourseSelectionDTO {

    private Long courseId;
    private ProfileKnowledgeLevel knowledgeLevel; 

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public ProfileKnowledgeLevel getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(ProfileKnowledgeLevel knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }    
}
