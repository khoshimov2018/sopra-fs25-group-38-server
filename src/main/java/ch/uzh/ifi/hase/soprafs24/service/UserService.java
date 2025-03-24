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

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    // Validate input: username and password must not be empty
    validateRegistrationInput(newUser);
    
    // Check if username already exists
    checkIfUsernameExists(newUser.getUsername());
    
    // Set creation date, token and status
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setCreationDate(LocalDateTime.now());
    
    // Save user
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }
  
  /**
   * Validates that the username and password are not empty
   * 
   * @param user the user to validate
   * @throws ResponseStatusException if username or password is empty
   */
  private void validateRegistrationInput(User user) {
    // Check if username is empty
    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty. Please provide a username.");
    }
    
    // Check if password is empty
    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty. Please provide a password.");
    }
  }
  
  /**
   * Checks if a username already exists in the database
   * 
   * @param username the username to check
   * @throws ResponseStatusException if username already exists
   */
  private void checkIfUsernameExists(String username) {
    User userByUsername = userRepository.findByUsername(username);
    
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists. Please choose another username.");
    }
  }
  
  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name defined in the User entity. The method will do nothing 
   * if the input is unique and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null && userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the name", "are"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    } else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
    }
  }
  
  /**
   * Logs in a user with the provided username and password
   *
   * @param username the username of the user
   * @param password the password of the user
   * @return the logged in user
   * @throws ResponseStatusException if login fails
   */
  public User loginUser(String username, String password) {
    // Find user by username
    User user = userRepository.findByUsername(username);
    
    // Validate user exists and password matches
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found. Please register first.");
    }
    
    if (!Objects.equals(user.getPassword(), password)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password. Please try again.");
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
   * Updates a user's profile (username and birthday)
   * 
   * @param userId the ID of the user to update
   * @param userInput the updated user data
   * @return the updated user
   * @throws ResponseStatusException if the user is not found or the update is invalid
   */
  public User updateUser(Long userId, User userInput) {
    // Find the user by ID
    User userToUpdate = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
            String.format("User with ID %d was not found", userId)));
    
    // Validate the username is not empty
    if (userInput.getUsername() == null || userInput.getUsername().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
          "Username cannot be empty. Please provide a username.");
    }
    
    // Check if the new username is unique (only if it's different from current)
    if (!Objects.equals(userToUpdate.getUsername(), userInput.getUsername())) {
      User existingUser = userRepository.findByUsername(userInput.getUsername());
      if (existingUser != null && !Objects.equals(existingUser.getId(), userId)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, 
            "Username already exists. Please choose another username.");
      }
      
      // Update username if valid
      userToUpdate.setUsername(userInput.getUsername());
    }
    
    // Update birthday (can be null)
    userToUpdate.setBirthday(userInput.getBirthday());
    
    // Save and return the updated user
    userToUpdate = userRepository.save(userToUpdate);
    userRepository.flush();
    
    return userToUpdate;
  }
}
