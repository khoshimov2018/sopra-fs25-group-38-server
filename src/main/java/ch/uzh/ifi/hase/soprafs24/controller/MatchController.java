package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



//exposes all matching endpoints 
@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/like")
    public ResponseEntity<MatchGetDTO> likeMatch(@RequestBody MatchPostDTO matchPostDTO) {
        MatchGetDTO result = matchService.processLike(matchPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/dislike")
    public ResponseEntity<Void> dislikeMatch(@RequestBody MatchPostDTO matchPostDTO) {
        matchService.processDislike(matchPostDTO);
        return ResponseEntity.ok().build();
    }
}
