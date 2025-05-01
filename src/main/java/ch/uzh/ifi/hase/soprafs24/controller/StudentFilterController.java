package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.CourseService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://sopra-fs25-group-38-client.vercel.app",
        "https://sopra-fs25-group-38-client-notsofuns-projects.vercel.app"})
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
            @RequestParam(required = false) List<String> availability,
            @RequestParam(defaultValue = "false") boolean matchAny) {

        List<User> allUsers = userService.getUsers();

        Set<Long> courseMatchedUserIds = resolveCourseMatches(courseIds, matchAny);
        Set<Long> availabilityMatchedUserIds = resolveAvailabilityMatches(availability);

        boolean hasCourseFilter = courseIds != null && !courseIds.isEmpty();
        boolean hasAvailabilityFilter = availability != null && !availability.isEmpty();

        return allUsers.stream()
                .filter(user -> shouldIncludeUser(user, courseMatchedUserIds, availabilityMatchedUserIds, hasCourseFilter, hasAvailabilityFilter))
                .map(DTOMapper.INSTANCE::convertEntityToUserGetDTO)
                .toList();

    }

    private Set<Long> resolveCourseMatches(List<Long> courseIds, boolean matchAny) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> matchedIds = matchAny
                ? courseService.findUserIdsByAnyCourseIds(courseIds)
                : courseService.findUserIdsEnrolledInAllCourses(courseIds);
        return new HashSet<>(matchedIds);
    }

    private Set<Long> resolveAvailabilityMatches(List<String> availability) {
        if (availability == null || availability.isEmpty()) {
            return Collections.emptySet();
        }
        List<UserAvailability> enums = availability.stream()
                .map(UserAvailability::valueOf)
                .toList();
        return new HashSet<>(courseService.findUserIdsEnrolledInAllAvailability(enums));
    }

    private boolean shouldIncludeUser(User user, Set<Long> courseIds, Set<Long> availabilityIds,
                                      boolean hasCourseFilter, boolean hasAvailabilityFilter) {
        Long userId = user.getId();
        if (userId == null || "admin@example.com".equalsIgnoreCase(user.getEmail())) {
            return false;
        }

        boolean matchesCourse = courseIds.isEmpty() || courseIds.contains(userId);
        boolean matchesAvailability = availabilityIds.isEmpty() || availabilityIds.contains(userId);

        return (hasCourseFilter && hasAvailabilityFilter && matchesCourse && matchesAvailability)
                || (hasCourseFilter && !hasAvailabilityFilter && matchesCourse)
                || (!hasCourseFilter && hasAvailabilityFilter && matchesAvailability)
                || (!hasCourseFilter && !hasAvailabilityFilter);
    }
}
