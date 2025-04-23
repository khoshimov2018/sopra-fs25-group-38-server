package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.ReportBlockService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/blocks")
public class BlockController {

    private final ReportBlockService reportBlockService;

    public BlockController(ReportBlockService reportBlockService) {
        this.reportBlockService = reportBlockService;
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
}
