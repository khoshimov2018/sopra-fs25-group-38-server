package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Block;
import ch.uzh.ifi.hase.soprafs24.entity.Report;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.BlockRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ReportRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportBlockServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatService chatService;

    @Mock
    private MatchService matchService;

    private ReportBlockService reportBlockService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        reportBlockService = new ReportBlockService(
                reportRepository,
                blockRepository,
                userRepository,
                chatService,
                matchService
        );
    }

    @Test
    void testReportUser_savesReport() {
        User reporter = new User();
        reporter.setId(1L);
        User reported = new User();
        reported.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reported));

        reportBlockService.reportUser(1L, 2L, "spam");

        verify(reportRepository, times(1)).save(any(Report.class));
        verify(blockRepository, times(1)).save(any(Block.class));
        verify(chatService, times(1)).deleteIndividualChannelBetweenUsers(1L, 2L);
        verify(matchService, times(1)).deleteMatchBetweenUsers(1L, 2L);
    }

    @Test
    void testBlockUser_savesBlock() {
        User blocker = new User();
        blocker.setId(1L);
        User blocked = new User();
        blocked.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(blocker));
        when(userRepository.findById(2L)).thenReturn(Optional.of(blocked));
        when(blockRepository.existsByBlockerIdAndBlockedUserId(1L, 2L)).thenReturn(false);

        reportBlockService.blockUser(1L, 2L);

        verify(blockRepository, times(1)).save(any(Block.class));
        verify(chatService, times(1)).deleteIndividualChannelBetweenUsers(1L, 2L);
        verify(matchService, times(1)).deleteMatchBetweenUsers(1L, 2L);
    }

    @Test
    void testBlockUser_duplicateThrowsConflict() {
        when(blockRepository.existsByBlockerIdAndBlockedUserId(1L, 2L)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> reportBlockService.blockUser(1L, 2L));
    }

    @Test
    void testReportUser_selfReportingThrowsError() {
        assertThrows(ResponseStatusException.class, () -> reportBlockService.reportUser(1L, 1L, "invalid"));
    }
}
