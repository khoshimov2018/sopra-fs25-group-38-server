package ch.uzh.ifi.hase.soprafs24.config;

import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public DataInitializer(CourseRepository courseRepository, 
                           UserRepository userRepository,
                           UserService userService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Check if courses already exist to avoid duplicates
        if (courseRepository.count() == 0) {
            List<Course> courses = List.of(
                new Course("AI"),
                new Course("Machine Learning"),
                new Course("Software Engineering"),
                new Course("Data Science"),
                new Course("Databases"),
                new Course("Web Development")
            );
            
            // List<Course> courses = List.of(
            //     new Course(1L, "AI"),
            //     new Course(2L, "Machine Learning"),
            //     new Course(3L, "Software Engineering"),
            //     new Course(4L, "Data Science"),
            //     new Course(5L, "Databases"),
            //     new Course(6L, "Web Development")
            // );
            courseRepository.saveAll(courses);
            log.info("Predefined courses inserted into the database.");
        } else {
            log.info("Courses already exist — skipping initialization.");
        }

        // Check if Admin account already exists
        if (!userRepository.existsByEmail("admin@example.com")) {
            UserPostDTO adminDTO = new UserPostDTO();
            adminDTO.setName("Admin");
            adminDTO.setEmail("admin@example.com");
            adminDTO.setPassword("securePassword123");
            adminDTO.setStudyLevel("None");
            adminDTO.setStudyGoals(List.of("System control"));

            User admin = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(adminDTO);
            User createdAdmin = userService.createUser(admin, null); 

            log.info("Admin account created with token: {}", createdAdmin.getToken());
        } else {
            log.info("Admin account already exists — skipping.");
        }
    }
}