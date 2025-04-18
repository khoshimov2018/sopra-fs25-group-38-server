package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<CourseGetDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseGetDTO> dtos = new ArrayList<>();
        for (Course c : courses) {
            dtos.add(DTOMapper.INSTANCE.convertEntityToCourseGetDTO(c));
        }
        return dtos;
    }
}

