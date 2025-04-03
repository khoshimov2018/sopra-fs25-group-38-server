package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Block;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.BlockRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/block")
public class BlockController {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

    public BlockController(BlockRepository blockRepository, UserRepository userRepository) {
        this.blockRepository = blockRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void blockUser(@RequestParam Long blockerId, @RequestParam Long blockedUserId) {
        // Check if both users exist
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocker user not found"));
        User blocked = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocked user not found"));

        // Prevent duplicate blocks
        if (blockRepository.existsByBlockerIdAndBlockedUserId(blockerId, blockedUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already blocked.");
        }

        // Create and save the block
        Block newBlock = new Block(blocker, blocked);
        blockRepository.save(newBlock);
    }
}
