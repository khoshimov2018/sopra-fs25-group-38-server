package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Match;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseGetDTO; 
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "studyLevel", target = "studyLevel")
  @Mapping(source = "studyGoals", target = "studyGoals")
  @Mapping(source = "profilePicture", target = "profilePicture")  
  @Mapping(source = "bio", target = "bio")
  @Mapping(source = "availability", target = "availability")
  @Mapping(source = "knowledgeLevel", target = "knowledgeLevel")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "availability", target = "availability")
  UserGetDTO convertEntityToUserGetDTO(User user);

  // Course mapping
  @Mapping(source = "id", target = "id")
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "courseName", target = "courseName")
  CourseGetDTO convertEntityToCourseGetDTO(Course course);

  // Match mappings

  @Mapping(source = "userId", target = "userId1")
  @Mapping(source = "targetUserId", target = "userId2")
  Match convertMatchPostDTOtoEntity(MatchPostDTO matchPostDTO);
  
  @Mapping(source = "id", target = "id")
  @Mapping(source = "userId1", target = "userId1")
  @Mapping(source = "userId2", target = "userId2")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "likedByUser1", target = "likedByUser1")
  @Mapping(source = "likedByUser2", target = "likedByUser2")
  MatchGetDTO convertEntityToMatchGetDTO(Match match);

  default List<String> map(UserAvailability availability) {
    if (availability == null) return new ArrayList<>();
    return List.of(availability.name());
  }

}
