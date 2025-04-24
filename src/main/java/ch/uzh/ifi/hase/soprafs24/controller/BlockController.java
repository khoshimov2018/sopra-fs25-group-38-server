package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.BlockDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.ReportBlockService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blocks")
public class BlockController {

    private final ReportBlockService reportBlockService;
    private final UserService userService;

    public BlockController(ReportBlockService reportBlockService, UserService userService) {
        this.reportBlockService = reportBlockService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void blockUser(@RequestBody Map<String, Long> payload) {
        Long blockerId = payload.get("blockerId");
        Long blockedUserId = payload.get("blockedUserId");

        if (blockerId == null || blockedUserId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both blockerId and blockedUserId must be provided.");
        }

        reportBlockService.blockUser(blockerId, blockedUserId);
    }

    // retreive block database for admin page
    @GetMapping
    public List<BlockDTO> getAllBlocks(@RequestHeader("Authorization") String token) {
        if (!userService.isAdmin(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view block data");
        }

        return reportBlockService.getAllBlocks().stream()
            .map(block -> DTOMapper.INSTANCE.convertToBlockDTO(block))
            .collect(Collectors.toList());
    }
}
