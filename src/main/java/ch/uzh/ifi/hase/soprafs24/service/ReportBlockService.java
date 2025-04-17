package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Block;
import ch.uzh.ifi.hase.soprafs24.entity.Report;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.BlockRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ReportRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ReportBlockService {

    private final ReportRepository reportRepository;
    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final MatchService matchService;

    @Autowired
    public ReportBlockService(ReportRepository reportRepository,
                              BlockRepository blockRepository,
                              UserRepository userRepository,
                              ChatService chatService,
                              MatchService matchService) {
        this.reportRepository = reportRepository;
        this.blockRepository = blockRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.matchService = matchService;
    }

    public void reportUser(Long reporterId, Long reportedUserId, String reason) {
        if (reporterId.equals(reportedUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot report yourself.");
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter not found"));
        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reported user not found"));

        Report report = new Report(reporter, reportedUser, reason);
        reportRepository.save(report);

        if (!blockRepository.existsByBlockerIdAndBlockedUserId(reporterId, reportedUserId)) {
            Block block = new Block(reporter, reportedUser);
            blockRepository.save(block);
        }

        chatService.deleteIndividualChannelBetweenUsers(reporterId, reportedUserId);
        matchService.deleteMatchBetweenUsers(reporterId, reportedUserId);
    }

    public void blockUser(Long blockerId, Long blockedUserId) {
        if (blockerId.equals(blockedUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot block yourself.");
        }

        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocker not found"));
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocked user not found"));

        if (blockRepository.existsByBlockerIdAndBlockedUserId(blockerId, blockedUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already blocked.");
        }

        Block block = new Block(blocker, blockedUser);
        blockRepository.save(block);
        chatService.deleteIndividualChannelBetweenUsers(blockerId, blockedUserId);
        matchService.deleteMatchBetweenUsers(blockerId, blockedUserId);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Block> getBlocksByBlocker(Long blockerId) {
        return blockRepository.findByBlockerId(blockerId);
    }

    public boolean isInteractionBlocked(Long userId1, Long userId2) {
        return blockRepository.existsByBlockerIdAndBlockedUserIdOrBlockedUserIdAndBlockerId(
                userId1, userId2, userId1, userId2
        );
    }
}
