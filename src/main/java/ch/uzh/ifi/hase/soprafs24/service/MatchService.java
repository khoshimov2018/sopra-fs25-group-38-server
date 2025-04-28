package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Match;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final NotificationService notificationService;
    private final DTOMapper dtoMapper = DTOMapper.INSTANCE;

    @Autowired
    public MatchService(MatchRepository matchRepository,
                        UserRepository userRepository,
                        ChatService chatService,
                        NotificationService notificationService) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.notificationService = notificationService;
    }
    /**
     * Processes a "like" action.
     * If a match record already exists between the two users, update the match to be ACCEPTED.
     * If no match exists, create a new Match record.
     */

    public Optional<Match> findMatchByUsers(Long userId, Long targetUserId) {
        return matchRepository.findMatchByUsers(userId, targetUserId);
    }
    

    public MatchGetDTO processLike(MatchPostDTO matchPostDTO) {
        // Try to find an existing match record regardless of order.
        //optional to handle no result from query  
        Optional<Match> existingMatch = findMatchByUsers(
                matchPostDTO.getUserId(), matchPostDTO.getTargetUserId());

        Match match;
        boolean wasAlreadyLikedByTargetUser = false;
        
        if (existingMatch.isPresent()) {
            match = existingMatch.get();
             // Check if the match has been rejected. This is used for blocking.
            if (match.getStatus() == MatchStatus.REJECTED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This match has been rejected due to a block.");
            }
            
            if (Objects.equals(match.getUserId1(), matchPostDTO.getUserId())) {
                wasAlreadyLikedByTargetUser = match.isLikedByUser2();
                match.setLikedByUser1(true);
            } else {
                wasAlreadyLikedByTargetUser = match.isLikedByUser1();
                match.setLikedByUser2(true);
            }
        } else {
            // No existing match: create a new one.
            match = dtoMapper.convertMatchPostDTOtoEntity(matchPostDTO);
            // Set the corresponding like flag (assuming the acting user is user1 in new records)
            // i.e. for a new match user1 is the person that first like the other person
            match.setLikedByUser1(true);
            match.setLikedByUser2(false);
            // Set an initial status (e.g., PENDING).
            match.setStatus(MatchStatus.PENDING);
            
            notificationService.createLikeNotification(matchPostDTO.getTargetUserId(), matchPostDTO.getUserId());
        }

        // Check if both users have liked each other and update status to ACCEPTED.
        if (match.isLikedByUser1() && match.isLikedByUser2()) {
            match.setStatus(MatchStatus.ACCEPTED);

            // Create an individual chat channel for this accepted match.
            User user1 = userRepository.findById(match.getUserId1())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User with id " + match.getUserId1() + " not found."));
            User user2 = userRepository.findById(match.getUserId2())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User with id " + match.getUserId2() + " not found."));

            // Call ChatService's method to create a channel.
            chatService.createIndividualChatChannelAfterMatch(user1, user2);
            notificationService.createMatchNotification(match.getUserId1(), match.getUserId2(), match.getId());
        } else if (!wasAlreadyLikedByTargetUser) {
            notificationService.createLikeNotification(matchPostDTO.getTargetUserId(), matchPostDTO.getUserId());
        }

        Match savedMatch = matchRepository.save(match);
        return dtoMapper.convertEntityToMatchGetDTO(savedMatch);
    }

    //process dislike action 
    public void processDislike(MatchPostDTO matchPostDTO) {
        // Try to find an existing match between the two users.
        Optional<Match> existingMatchOptional = findMatchByUsers(
                matchPostDTO.getUserId(), matchPostDTO.getTargetUserId());
    
        if (existingMatchOptional.isPresent()) {
            Match match = existingMatchOptional.get();
    
            if (Objects.equals(match.getUserId1(), matchPostDTO.getUserId())) {
                match.setLikedByUser1(false);
            } else if (Objects.equals(match.getUserId2(), matchPostDTO.getUserId())) {
                match.setLikedByUser2(false);
            }
            //set status to REJECTED
            match.setStatus(MatchStatus.REJECTED);
            matchRepository.save(match);
        } else {
            // If no match exists, create one with rejected status
            Match newMatch = new Match();
            newMatch.setUserId1(matchPostDTO.getUserId());
            newMatch.setUserId2(matchPostDTO.getTargetUserId());
            newMatch.setStatus(MatchStatus.REJECTED);
            newMatch.setLikedByUser1(false);
            newMatch.setLikedByUser2(false);
            matchRepository.save(newMatch);
        }
    }

    public void deleteMatchBetweenUsers(Long userAId, Long userBId) {
        Optional<Match> matchOptional = matchRepository.findMatchByUsers(userAId, userBId);
        matchOptional.ifPresent(matchRepository::delete);
    }
}
