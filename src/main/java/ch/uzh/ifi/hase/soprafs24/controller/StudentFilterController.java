package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.CourseService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/students")
public class StudentFilterController {

    private final UserService userService;
    private final CourseService courseService;

    public StudentFilterController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserGetDTO> getFilteredStudents(
            @RequestParam(required = false) List<Long> courseIds,
            @RequestParam(required = false) List<UserAvailability> availability) {

        List<User> allUsers = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        List<Long> courseMatchedUserIds = new ArrayList<>();
        if (courseIds != null && !courseIds.isEmpty()) {
            courseMatchedUserIds = courseService.findUserIdsEnrolledInAllCourses(courseIds);
        }

        List<Long> availabilityMatchedUserIds = new ArrayList<>();
        if (availability != null && !availability.isEmpty()) {
            availabilityMatchedUserIds = courseService.findUserIdsEnrolledInAllAvailability(availability);
        }

        // Filter with OR condition
        for (User user : allUsers) {
            Long userId = user.getId();
            if (courseMatchedUserIds.contains(userId) || availabilityMatchedUserIds.contains(userId)) {
                userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            }
        }

        return userGetDTOs;
    }
}
