package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Match; 
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "birthday", target = "birthday")
  UserGetDTO convertEntityToUserGetDTO(User user);


  // Match mappings

    @Mapping(source = "userId", target = "userId1")
    @Mapping(source = "targetUserId", target = "userId2")
    Match convertMatchPostDTOtoEntity(MatchPostDTO matchPostDTO);

    // For GET requests, we expose the basic match details.
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId1", target = "userId1")
    @Mapping(source = "userId2", target = "userId2")
    @Mapping(source = "status", target = "status")
    MatchGetDTO convertEntityToMatchGetDTO(Match match);

}
