package ch.uzh.ifi.hase.soprafs24.rest.dto;

// used to return chat participant information

public class ChatParticipantGetDTO {
    private Long userId;
    private String userName;
    private String userProfileImage;
    private String role;

    // getter and setter
    
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserProfileImage() {
        return userProfileImage;
    }
    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
