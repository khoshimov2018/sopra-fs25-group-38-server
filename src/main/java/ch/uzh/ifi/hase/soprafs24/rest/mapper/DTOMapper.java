package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    // --- USER MAPPINGS ---

    @Mapping(source = "studyGoals", target = "studyGoals", qualifiedByName = "joinStudyGoals")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(target = "userCourses", expression = "java(convertUserCourses(user.getUserCourses()))")
    @Mapping(target = "studyGoals", expression = "java(splitStudyGoals(user.getStudyGoals()))")
    UserGetDTO convertEntityToUserGetDTO(User user);

    // --- COURSE MAPPING ---

    @Mapping(source = "id", target = "id")
    @Mapping(source = "courseName", target = "courseName")
    CourseGetDTO convertEntityToCourseGetDTO(Course course);

    // --- USER PROFILE UPDATING ---

    default void updateUserFromDTO(UserPutDTO userPutDTO, User user, CourseRepository courseRepository) {
        if (userPutDTO.getName() != null) user.setName(userPutDTO.getName());
        if (userPutDTO.getBio() != null) user.setBio(userPutDTO.getBio());
        if (userPutDTO.getProfilePicture() != null) user.setProfilePicture(userPutDTO.getProfilePicture());
        if (userPutDTO.getAvailability() != null) user.setAvailability(userPutDTO.getAvailability());
        if (userPutDTO.getStudyLevel() != null) user.setStudyLevel(userPutDTO.getStudyLevel());
        if (userPutDTO.getStudyGoals() != null) user.setStudyGoals(joinStudyGoals(userPutDTO.getStudyGoals()));

        if (userPutDTO.getCourseSelections() != null) {
            user.getUserCourses().clear();
            for (CourseSelectionDTO selection : userPutDTO.getCourseSelections()) {
                Course course = courseRepository.findById(selection.getCourseId())
                        .orElseThrow(() -> new IllegalArgumentException("Course not found: " + selection.getCourseId()));

                UserCourse userCourse = new UserCourse();
                userCourse.setUser(user);
                userCourse.setCourse(course);
                userCourse.setKnowledgeLevel(selection.getKnowledgeLevel());
                user.getUserCourses().add(userCourse);
            }
        }
    }

    // --- STUDY GOALS MAPPING HELPERS ---

    @Named("joinStudyGoals")
    default String joinStudyGoals(List<String> goals) {
        return goals == null ? null : String.join(",", goals);
    }

    default List<String> splitStudyGoals(String goals) {
        return goals == null || goals.isBlank() ? new ArrayList<>() : List.of(goals.split("\\s*,\\s*"));
    }

    // --- USER COURSE MAPPING ---

    default List<UserGetDTO.UserCourseDTO> convertUserCourses(List<UserCourse> userCourses) {
        List<UserGetDTO.UserCourseDTO> dtos = new ArrayList<>();
        if (userCourses != null) {
            for (UserCourse uc : userCourses) {
                dtos.add(mapUserCourseToDTO(uc));
            }
        }
        return dtos;
    }

    default UserGetDTO.UserCourseDTO mapUserCourseToDTO(UserCourse userCourse) {
        if (userCourse == null) return null;
        UserGetDTO.UserCourseDTO dto = new UserGetDTO.UserCourseDTO();
        dto.setCourseId(userCourse.getCourse().getId());
        dto.setCourseName(userCourse.getCourse().getCourseName());
        dto.setKnowledgeLevel(userCourse.getKnowledgeLevel());
        return dto;
    }

    // --- CHAT MAPPINGS ---

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "channelName")
    @Mapping(target = "type", source = "channelType")
    @Mapping(target = "channelProfileImage", source = "channelProfileImage")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "participants", ignore = true)
    ChatChannel convertChatChannelPostDTOtoEntity(ChatChannelPostDTO chatChannelPostDTO);

    @Mapping(source = "id", target = "channelId")
    @Mapping(source = "name", target = "channelName")
    @Mapping(source = "type", target = "channelType")
    @Mapping(source = "channelProfileImage", target = "channelProfileImage")
    @Mapping(source = "participants", target = "participants")
    @Mapping(target = "createdAt", expression = "java(chatChannel.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(chatChannel.getUpdatedAt().toString())")
    ChatChannelGetDTO convertEntityToChatChannelGetDTO(ChatChannel chatChannel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "channel", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "content", source = "context")
    Message convertMessagePostDTOtoEntity(MessagePostDTO messagePostDTO);

    @Mapping(source = "id", target = "messageId")
    @Mapping(source = "channel.id", target = "channelId")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "content", target = "context")
    @Mapping(target = "timestamp", expression = "java(message.getTimestamp().toString())")
    @Mapping(source = "sender.profilePicture", target = "senderProfileImage")
    MessageGetDTO convertEntityToMessageGetDTO(Message message);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.profilePicture", target = "userProfileImage")
    @Mapping(source = "role", target = "role")
    ChatParticipantGetDTO convertEntityToChatParticipantGetDTO(ChatParticipant chatParticipant);

    List<ChatParticipantGetDTO> convertEntityToChatParticipantGetDTOList(List<ChatParticipant> chatParticipants);

    // --- MATCH MAPPINGS ---

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
}
