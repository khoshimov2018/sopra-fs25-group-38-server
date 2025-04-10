package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserCourse;
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
            // firstly, receive the availability as string.
            @RequestParam(required = false) List<String> availability) {

        List<User> allUsers = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        List<Long> courseMatchedUserIds = new ArrayList<>();
        if (courseIds != null && !courseIds.isEmpty()) {
            courseMatchedUserIds = courseService.findUserIdsEnrolledInAllCourses(courseIds);
        }

        List<UserAvailability> availabilityEnums = new ArrayList<>();
        List<Long> availabilityMatchedUserIds = new ArrayList<>();
        if (availability != null && !availability.isEmpty()) {
            for (String str : availability) {

                // Second, convert the availability values from String to Enum to allow proper binding in the JPQL query.
                availabilityEnums.add(UserAvailability.valueOf(str));
            }
            availabilityMatchedUserIds = courseService.findUserIdsEnrolledInAllAvailability(availabilityEnums);
        }

        boolean hasCourseFilter = courseIds != null && !courseIds.isEmpty();
        boolean hasAvailabilityFilter = availability != null && !availability.isEmpty();

        for (User user : allUsers) {
            Long userId = user.getId();
            if (userId == null) continue;

            boolean matchesCourse = courseMatchedUserIds.isEmpty() || courseMatchedUserIds.contains(userId);
            boolean matchesAvailability = availabilityMatchedUserIds.isEmpty() || availabilityMatchedUserIds.contains(userId);

            boolean shouldIncludeUser =
                    (hasCourseFilter && hasAvailabilityFilter && matchesCourse && matchesAvailability)
                || (hasCourseFilter && !hasAvailabilityFilter && matchesCourse)
                || (!hasCourseFilter && hasAvailabilityFilter && matchesAvailability)
                || (!hasCourseFilter && !hasAvailabilityFilter);

            if (shouldIncludeUser) {
                userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            }
        }

        return userGetDTOs;
    }
}
