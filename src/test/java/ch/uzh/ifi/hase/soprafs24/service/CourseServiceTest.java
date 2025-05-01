package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test: Service Method - findUserIdsByAnyCourseIds
 * Purpose: `findUserIdsByAnyCourseIds` calls the repository and returns the expected results.
 * Scenario: mock the repository call and validate that the result returned from the service is correct.
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void testFindUserIdsByAnyCourseIds() {
        // Given: List of course IDs to search for.
        List<Long> ids = List.of(1L, 2L);
        // Mock: Repository method returns a list of user IDs associated with the course IDs.
        when(courseRepository.findUserIdsByAnyCourseIds(ids)).thenReturn(List.of(100L));

        // When: Calling the service method.
        List<Long> result = courseService.findUserIdsByAnyCourseIds(ids);

        // Then: Verify that the result matches the expected list of user IDs.
        assertEquals(List.of(100L), result);
    }
}
