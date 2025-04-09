package ch.uzh.ifi.hase.soprafs24.config;

import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final CourseRepository courseRepository;

    public DataInitializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Check if courses already exist to avoid duplicates
        if (courseRepository.count() == 0) {
            List<Course> courses = List.of(
                new Course(1L, "AI"),
                new Course(2L, "Machine Learning"),
                new Course(3L, "Software Engineering"),
                new Course(4L, "Data Science"),
                new Course(5L, "Databases"),
                new Course(6L, "Web Development")
            );

            courseRepository.saveAll(courses);
            System.out.println("Predefined courses inserted into the database.");
        } else {
            System.out.println("Courses already exist — skipping initialization.");
        }
    }
}

