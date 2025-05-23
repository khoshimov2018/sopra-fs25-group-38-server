package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserCourse;
import ch.uzh.ifi.hase.soprafs24.entity.Block;
import ch.uzh.ifi.hase.soprafs24.entity.ChatChannel;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.Match;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import ch.uzh.ifi.hase.soprafs24.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ProfileRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ReportRepository;
import ch.uzh.ifi.hase.soprafs24.repository.StudyPlanRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserCourseRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.uzh.ifi.hase.soprafs24.repository.BlockRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatChannelRepository;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.Set;
import java.util.HashSet;





/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String USER_NOT_FOUND = "User not found";

  private final UserRepository userRepository;
  private final MatchRepository matchRepository;
  private final CourseRepository courseRepository;
  private final UserCourseRepository userCourseRepository;
  private final MessageRepository messageRepository;
  private final BlockRepository blockRepository;
  private final ReportRepository reportRepository;
  private final ChatChannelRepository chatChannelRepository;
  private final StudyPlanRepository studyPlanRepository;
  private final ProfileRepository profileRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final ChatService chatService;

  @Autowired
  public UserService(UserRepository userRepository,
                     MatchRepository matchRepository,
                     CourseRepository courseRepository,
                     UserCourseRepository userCourseRepository,
                     MessageRepository messageRepository,
                     BlockRepository blockRepository,
                     ReportRepository reportRepository,
                     ChatChannelRepository chatChannelRepository,
                     StudyPlanRepository studyPlanRepository,
                     ProfileRepository profileRepository,
                     ChatService chatService) {
    this.userRepository = userRepository;
    this.matchRepository = matchRepository;
    this.courseRepository = courseRepository;
    this.userCourseRepository = userCourseRepository;
    this.messageRepository = messageRepository;
    this.blockRepository = blockRepository;
    this.reportRepository = reportRepository;
    this.chatChannelRepository = chatChannelRepository;
    this.studyPlanRepository = studyPlanRepository;
    this.profileRepository = profileRepository;
    this.chatService = chatService;
  }

  public List<Long> getBlockedOrBlockingUserIds(Long userId) {
      List<Long> blocked = blockRepository.findByBlockerId(userId)
          .stream().map(Block::getBlockedUserId).toList();

      List<Long> blocking = blockRepository.findByBlockedUserId(userId)
          .stream().map(Block::getBlockerId).toList();

      return Stream.concat(blocked.stream(), blocking.stream())
          .distinct()
          .toList();
  }


  public List<User> getDiscoverableUsers(Long currentUserId) {
    List<Long> excludedUserIds = getBlockedOrBlockingUserIds(currentUserId);
    return userRepository.findDiscoverableUsers(currentUserId, excludedUserIds);
  }

  

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser, List<CourseSelectionDTO> courseSelections) {
    // Validate input: email and password must not be empty
    validateRegistrationInput(newUser);

    
    //Check if email already exists
    if (userRepository.existsByEmail(newUser.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists. Please choose another email.");
    }
    

    // Hash password
    String hashedPassword = passwordEncoder.encode(newUser.getPassword());
    newUser.setPassword(hashedPassword);

    // Set creation date, token and status
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDateTime.now());
    
  
    if (newUser.getUserCourses() != null) {
    for (UserCourse uc : newUser.getUserCourses()) {
        uc.setUser(newUser); // link back to user
      }
  }

    // Save user
    newUser = userRepository.save(newUser);
    userRepository.flush();
//////---------------------------------------------
    // Mapping with User and courseSelections
    if (courseSelections != null && !courseSelections.isEmpty()) {
      assignCoursesWithKnowledgeLevels(newUser, courseSelections);
  }

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }
  
  /**
   * Validates that the email and password are not empty
   * 
   * @param user the user to validate
   * @throws ResponseStatusException if email or password is empty
   */
  private void validateRegistrationInput(User user) {
    // Check if email is empty
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be empty. Please provide an email.");
    }
    
    // Check if password is empty
    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty. Please provide a password.");
    }
    // Check password length
    if (user.getPassword().length() < 8) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters long.");
    }

    // Validate required fields
    if (user.getStudyLevel() == null || user.getStudyLevel().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Study level is required.");
    }

    if (user.getStudyGoals() == null || user.getStudyGoals().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Study goals are required.");
    }
  }
  
  /**
   * Logs in a user with the provided email and password
   *
   * @param email the email of the user
   * @param password the password of the user
   * @return the logged in user
   * @throws ResponseStatusException if login fails
   */
  public User loginUser(String email, String password) {
    // Find user by email
    User user = userRepository.findByEmail(email);
    
    // Validate user exists and password matches
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found. Please register first.");
    }

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password. Please try again.");
    }
    
    // Set user status to online and generate a new token
    user.setStatus(UserStatus.ONLINE);
    user.setToken(UUID.randomUUID().toString());
    
    // Save updated user
    user = userRepository.save(user);
    userRepository.flush();
    
    return user;
  }
  
  /**
   * Logs out a user by setting their status to OFFLINE
   *
   * @param userId the ID of the user to log out
   * @throws ResponseStatusException if user not found
   */
  public void logoutUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    
    user.setStatus(UserStatus.OFFLINE);
    userRepository.save(user);
    userRepository.flush();
  }
  
  /**
   * Finds a user by their ID
   *
   * @param userId the ID of the user
   * @return the user
   * @throws ResponseStatusException if user not found
   */
  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
}

  
  //find userIds for matches
  public Set<Long> getMatchIdsForUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
    return user.getMatchIds();
  }
  
  public Set<Long> getAcceptedMatchPartnerIds(Long userId) {
    List<Match> acceptedMatches = matchRepository.findAcceptedMatchesByUserId(userId);
    Set<Long> partnerIds = new HashSet<>();
    for (Match match : acceptedMatches) {
        if (Objects.equals(match.getUserId1(), userId)) {
          partnerIds.add(match.getUserId2());
      } else {
          partnerIds.add(match.getUserId1());
      }
    }
    return partnerIds;
}

  /**
   * Authenticates a request based on token
   *
   * @param token the auth token from the request header
   * @throws ResponseStatusException if token is invalid
   */
  public void authenticateByToken(String token) {
    // Check if token exists
    if (token == null || token.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authentication token provided");
    }
    
    // Clean the token (remove "Bearer " prefix if exists)
    if (token.startsWith(BEARER_PREFIX)) {
      token = token.substring(7);
    }
    
    // Check if token is valid
    boolean tokenExists = userRepository.existsByToken(token);
    if (!tokenExists) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
    }
  }
  
  /**
   * Gets a user by their authentication token
   *
   * @param token the auth token without "Bearer " prefix
   * @return the User associated with this token, or null if not found
   */
  public User getUserByToken(String token) {
    // Check if token exists
    if (token == null || token.isEmpty()) {
      return null;
    }
    
    // Clean the token (remove "Bearer " prefix if exists)
    if (token.startsWith(BEARER_PREFIX)) {
      token = token.substring(7);
    }
    
    // Find user by token
    return userRepository.findByToken(token);
  }
  
  /**
   * Checks if the authenticated user is authorized to modify a specific user
   * 
   * @param token the auth token
   * @param userId the ID of the user to modify
   * @throws ResponseStatusException if the token owner is not authorized
   */
  public void checkAuthorizationById(String token, Long userId) {
    // Clean the token (remove "Bearer " prefix if exists)
    if (token != null && token.startsWith(BEARER_PREFIX)) {
      token = token.substring(7);
    }
    
    // Find user by token
    User tokenOwner = userRepository.findByToken(token);
    if (tokenOwner == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
    }
    
    // Check if token owner is the same as the user being modified
    if (!Objects.equals(tokenOwner.getId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
          "You are not authorized to modify this user's profile");
    }
  }
  
  @Transactional
  public User updateUser(Long userId, User updatedUser) {

    if (updatedUser.getAvailability() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability is required.");
    }
    if (updatedUser.getStudyGoals() == null || updatedUser.getStudyGoals().trim().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Study goals are required.");
    }
    if (updatedUser.getUserCourses() == null || updatedUser.getUserCourses().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one course must be selected.");
    }

    return userRepository.saveAndFlush(updatedUser);
  }

  


  
  /**
   * Logs out a user using the token by setting their status to OFFLINE
   *
   * @param token the Bearer token from Authorization header
   * @throws ResponseStatusException if token is invalid or user not found
   */
  public void logoutUserByToken(String token) {
    log.info(">>> Incoming token: {}", token);
  
    if (token != null && token.startsWith(BEARER_PREFIX)) {
      token = token.substring(7);
    }
  
    User user = userRepository.findByToken(token);
  
    if (user == null) {
      log.warn(">>> No user found for this token!");
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token.");
    }
  
    log.info(">>> Logging out user: {}", user.getEmail());
  
    user.setStatus(UserStatus.OFFLINE);
    user.setToken(null);
    userRepository.save(user);
    userRepository.flush();
  
    log.info(">>> Status set to OFFLINE and token cleared for: {}", user.getEmail());
  }
  


  public void assignCoursesWithKnowledgeLevels(User user, List<CourseSelectionDTO> courseSelections) {
    List<UserCourse> userCourses = courseSelections.stream().map(selection -> {
        Course course = courseRepository.findById(selection.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + selection.getCourseId()));

        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setKnowledgeLevel(selection.getKnowledgeLevel()); // Directly set enum

        return userCourse;
    }).toList();

    // add for mapping usercourse list
    user.getUserCourses().addAll(userCourses);
    userCourseRepository.saveAll(userCourses);
}

  /**
   * Deletes a user account by their user ID.
   * 
   * This method is intended to be used **only by administrators** to delete other users' accounts.
   * It performs full cleanup of all associated user data.
   *
   * @param userId the ID of the user to delete
   * @throws ResponseStatusException if the user with given ID is not found
   */
  public void deleteUserById(Long userId){
      User user = userRepository.findById(userId).orElseThrow(() 
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
      deleteUser(user); 
    } 

  /**
   * Deletes the currently authenticated user's account and all associated data.
   * 
   * This method is used by the user to delete **their own account** based on their authentication token.
   * It performs full cleanup of all associated user data.
    * @param token the Bearer authentication token of the user
    * @throws ResponseStatusException if the token is invalid or the user is not found
    */
    public void deleteUserByToken(String token) {
      if (token != null && token.startsWith(BEARER_PREFIX)) {
          token = token.substring(7);
      }

      User user = userRepository.findByToken(token);
      if (user == null) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token.");
      }

      deleteUser(user);
    }

  /**
   * Performs the actual deletion logic for a given user entity.
   *
   * It includes deletion of messages, matches, chat participation, blocks, reports, 
   * study plans, profile, enrolled courses, and the user account itself.
   *
   * @param user the User entity to be deleted
   */
  private void deleteUser(User user){
      Long userId = user.getId();

      chatService.removeAllUserChats(userId);
      // messageRepository.deleteAllBySenderId(userId);
      matchRepository.deleteAllByUserId1OrUserId2(userId, userId);

      blockRepository.deleteAllByBlockerIdOrBlockedUserId(userId, userId);
      reportRepository.deleteAllByReporterIdOrReportedUserId(userId, userId);

      studyPlanRepository.deleteAllByUserId(userId);
      profileRepository.deleteByUserId(userId);
      userCourseRepository.deleteAllByUserId(userId);

      userRepository.delete(user);
      userRepository.flush();
    }

  /**
   * Checks whether the given authentication token belongs to the administrator.
   *
   * @param token the Bearer authentication token from the Authorization header
   * @return true if the token belongs to the admin account, false otherwise
   * @throws ResponseStatusException if the token is invalid or user not found
   */public boolean isAdmin(String token) {
      if (token != null && token.startsWith(BEARER_PREFIX)) {
          token = token.substring(7);
      }

      User user = userRepository.findByToken(token);
      if (user == null) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
      }

      return "admin@example.com".equals(user.getEmail()); 
    }
}
