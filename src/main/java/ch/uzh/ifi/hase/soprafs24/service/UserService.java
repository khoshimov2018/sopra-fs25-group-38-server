package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


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

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
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
    
    // Save user
    newUser = userRepository.save(newUser);
    userRepository.flush();

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
   * Checks if a email already exists in the database
   * 
   * @param email the email to check
   * @throws ResponseStatusException if email already exists
   */
  private void checkIfEmailExists(String email) {
    User userByEmail = userRepository.findByEmail(email);
    
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists. Please choose another email.");
    }
  }
  
  /**
   * This is a helper method that will check the uniqueness criteria of the
   * email and the name defined in the User entity. The method will do nothing 
   * if the input is unique and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByEmail = userRepository.findByEmail(userToBeCreated.getEmail());
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByEmail != null && userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "email and the name", "are"));
    } else if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "email", "is"));
    } else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
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
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
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
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
            String.format("User with ID %d was not found", userId)));
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
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    
    // Check if token is valid
    boolean tokenExists = userRepository.existsByToken(token);
    if (!tokenExists) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
    }
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
    if (token != null && token.startsWith("Bearer ")) {
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
  
  /**
   * Updates a user's profile
   * 
   * @param userId the ID of the user to update
   * @param userInput the updated user data
   * @return the updated user
   * @throws ResponseStatusException if the user is not found or the update is invalid
   */
  public User updateUser(Long userId, User userInput) {
    User userToUpdate = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format("User with ID %d was not found", userId)));
  
    // Validate required fields
    if (userInput.getAvailability() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability is required.");
    }
  
    if (userInput.getKnowledgeLevel() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Knowledge level is required.");
    }
  
    if (userInput.getStudyGoals() == null || userInput.getStudyGoals().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Study goals are required.");
    }
  
    // Update optional and required fields
    userToUpdate.setBio(userInput.getBio());
    userToUpdate.setAvailability(userInput.getAvailability());
    userToUpdate.setKnowledgeLevel(userInput.getKnowledgeLevel());
    userToUpdate.setStudyGoals(userInput.getStudyGoals());
  
    // Optional: also allow updating profile picture or study level
    if (userInput.getStudyLevel() != null) {
      userToUpdate.setStudyLevel(userInput.getStudyLevel());
    }
    if (userInput.getProfilePicture() != null) {
      userToUpdate.setProfilePicture(userInput.getProfilePicture());
    }
  
    // Save updated user
    userToUpdate = userRepository.save(userToUpdate);
    userRepository.flush();
  
    return userToUpdate;
  }
  
  /**
   * Logs out a user using the token by setting their status to OFFLINE
   *
   * @param token the Bearer token from Authorization header
   * @throws ResponseStatusException if token is invalid or user not found
   */
  public void logoutUserByToken(String token) {
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    User user = userRepository.findByToken(token);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token.");
    }

    user.setStatus(UserStatus.OFFLINE);
    userRepository.save(user);
    userRepository.flush();
  }

}
