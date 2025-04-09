// import ch.uzh.ifi.hase.soprafs24.constant.MatchStatus;
// import ch.uzh.ifi.hase.soprafs24.entity.Match;
// import ch.uzh.ifi.hase.soprafs24.entity.User;
// import ch.uzh.ifi.hase.soprafs24.repository.MatchRepository;
// import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchGetDTO;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchPostDTO;
// import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
// import ch.uzh.ifi.hase.soprafs24.service.ChatService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.server.ResponseStatusException;

// import java.util.List;
// import java.util.Objects;
// import java.util.Optional;
// import java.util.Set;
// import java.util.HashSet;

// @Service
// @Transactional
// public class MatchService {

//     private final MatchRepository matchRepository;
//     private final UserRepository userRepository;
//     private final ChatService chatService;
//     private final DTOMapper dtoMapper = DTOMapper.INSTANCE;

//     @Autowired
//     public MatchService(MatchRepository matchRepository,
//                         UserRepository userRepository,
//                         ChatService chatService) {
//         this.matchRepository = matchRepository;
//         this.userRepository = userRepository;
//         this.chatService = chatService;
//     }
//     /**
//      * Processes a "like" action.
//      * If a match record already exists between the two users, update the match to be ACCEPTED.
//      * If no match exists, create a new Match record.
//      */
//     public MatchGetDTO processLike(MatchPostDTO matchPostDTO) {
//         // Try to find an existing match record regardless of order.
//         Optional<Match> existingMatch = matchRepository.findMatchByUsers(
//                 matchPostDTO.getUserId(), matchPostDTO.getTargetUserId());

//         Match match;
//         if (existingMatch.isPresent()) {
//             match = existingMatch.get();
//              // Check if the match has been rejected. This is used for blocking.
//             if (match.getStatus() == MatchStatus.REJECTED) {
//                 // You can either throw an exception or simply return the rejected match.
//                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This match has been rejected due to a block.");
//             }
//             // Determine which flag to update based on the acting user.
//             if (Objects.equals(match.getUser1Id(), matchPostDTO.getUserId())) {
//                 match.setLikedByUser1(true);
//             } else {
//                 match.setLikedByUser2(true);
//             }
//         } else {
//             // No existing match: create a new one.
//             match = dtoMapper.convertMatchPostDTOtoEntity(matchPostDTO);
//             // Set the corresponding like flag (assuming the acting user is user1 in new records)
//             // i.e. for a new match user1 is the person that first like the other person
//             match.setLikedByUser1(true);
//             match.setLikedByUser2(false);
//             // Set an initial status (e.g., PENDING).
//             match.setStatus(MatchStatus.PENDING);
//         }

//         // Check if both users have liked each other and update status to ACCEPTED.
//         if (match.isLikedByUser1() && match.isLikedByUser2() &&
//             match.getStatus() != MatchStatus.REJECTED) {
//             match.setStatus(MatchStatus.ACCEPTED);

//             // Create an individual chat channel for this accepted match.
//             // Retrieve both user entities (ensure they exist).
//             User user1 = userRepository.findById(match.getUser1Id())
//                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
//                         "User with id " + match.getUser1Id() + " not found."));
//             User user2 = userRepository.findById(match.getUser2Id())
//                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
//                         "User with id " + match.getUser2Id() + " not found."));

//             // Call ChatService's method to create a channel.
//             chatService.createIndividualChatChannelAfterMatch(user1, user2);
//         }


//         Match savedMatch = matchRepository.save(match);
//         return dtoMapper.convertEntityToMatchGetDTO(savedMatch);
//     }

//     /**
//      * Processes a "dislike" action.
//      * For this example, we'll simply remove any existing match record.
//      * You could also update a flag to mark a dislike, depending on requirements.
//      */
//     public void processDislike(MatchPostDTO matchPostDTO) {
//         Optional<Match> existingMatch = matchRepository.findMatchByUsers(
//                 matchPostDTO.getUserId(), matchPostDTO.getTargetUserId());
//         existingMatch.ifPresent(matchRepository::delete);
//     }
// }
