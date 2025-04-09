package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Match;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseGetDTO; 
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchGetDTO; 
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import ch.uzh.ifi.hase.soprafs24.entity.UserCourse;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
// import for chat system
import ch.uzh.ifi.hase.soprafs24.entity.ChatChannel;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatParticipantGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessageGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO; 

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
  @Mapping(source = "courseName", target = "courseName")
  CourseGetDTO convertEntityToCourseGetDTO(ch.uzh.ifi.hase.soprafs24.entity.Course course);
  
  // Chat mappings

  // Convert POST DTO to ChatChannel entity
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", source = "channelName")
  @Mapping(target = "type", source = "channelType")
  @Mapping(target = "channelProfileImage", source = "channelProfileImage")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "participants", ignore = true) // will be set in ChatService
  ChatChannel convertChatChannelPostDTOtoEntity(ChatChannelPostDTO chatChannelPostDTO);

  // Convert ChatChannel entity to GET DTO.
  @Mapping(source = "id", target = "channelId")
  @Mapping(source = "name", target = "channelName")
  @Mapping(source = "type", target = "channelType")
  @Mapping(source = "channelProfileImage", target = "channelProfileImage")
  @Mapping(source = "participants", target = "participants")
  @Mapping(target = "createdAt", expression = "java(chatChannel.getCreatedAt().toString())")
  @Mapping(target = "updatedAt", expression = "java(chatChannel.getUpdatedAt().toString())")
  ChatChannelGetDTO convertEntityToChatChannelGetDTO(ChatChannel chatChannel);

  // Convert MessagePostDTO to Message entity.
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "channel", ignore = true)
  @Mapping(target = "sender", ignore = true)
  @Mapping(target = "content", source = "context")
  Message convertMessagePostDTOtoEntity(MessagePostDTO messagePostDTO);

  // Convert Message entity to MessageGetDTO.
  @Mapping(source = "id", target = "messageId")
  @Mapping(source = "channel.id", target = "channelId")
  @Mapping(source = "sender.id", target = "senderId")
  @Mapping(source = "content", target = "context")
  @Mapping(target = "timestamp", expression = "java(message.getTimestamp().toString())")
  @Mapping(source = "sender.profilePicture", target = "senderProfileImage")
  MessageGetDTO convertEntityToMessageGetDTO(Message message);

  // Convert ChatParticipant entity to ChatParticipantGetDTO.
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.name", target = "userName")
  @Mapping(source = "user.profilePicture", target = "userProfileImage")
  @Mapping(source = "role", target = "role")
  ChatParticipantGetDTO convertEntityToChatParticipantGetDTO(ch.uzh.ifi.hase.soprafs24.entity.ChatParticipant chatParticipant);

  // Convert list of ChatParticipant entities to list of ChatParticipantGetDTOs.
  List<ChatParticipantGetDTO> convertEntityToChatParticipantGetDTOList(List<ch.uzh.ifi.hase.soprafs24.entity.ChatParticipant> chatParticipants);
 

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

  default String map(List<String> goals) {
    return goals == null ? null : String.join(",", goals);
}

default void updateUserFromDTO(UserPutDTO userPutDTO, User user, CourseRepository courseRepository) {
    if (userPutDTO.getName() != null) {
        user.setName(userPutDTO.getName());
    }
    if (userPutDTO.getBio() != null) {
        user.setBio(userPutDTO.getBio());
    }
    if (userPutDTO.getProfilePicture() != null) {
        user.setProfilePicture(userPutDTO.getProfilePicture());
    }
    if (userPutDTO.getAvailability() != null) {
        user.setAvailability(userPutDTO.getAvailability());
    }
    if (userPutDTO.getStudyLevel() != null) {
        user.setStudyLevel(userPutDTO.getStudyLevel());
    }
    if (userPutDTO.getStudyGoals() != null) {
        user.setStudyGoals(String.join(",", userPutDTO.getStudyGoals()));
    }

     // Update courses and their knowledge level
     if (userPutDTO.getCourses() != null) {
        user.getUserCourses().clear(); // Remove old links

        for (UserPutDTO.CourseSelectionDTO selection : userPutDTO.getCourses()) {
            Course course = courseRepository.findById(selection.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + selection.getCourseId()));

            UserCourse userCourse = new UserCourse();
            userCourse.setUser(user);
            userCourse.setCourse(course);
            userCourse.setKnowledgeLevel(selection.getKnowledgeLevel());

            user.getUserCourses().add(userCourse);
        }
    }


    // Do not set userCourses here — you'll do it inside the UserService
}


}
