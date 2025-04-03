package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Report;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ReportRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportController(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void reportUser(@RequestParam Long reporterId,
                           @RequestParam Long reportedUserId,
                           @RequestParam String reason) {
        // Validate users
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter user not found"));
        User reported = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reported user not found"));

        // Prevent duplicate reports
        if (reportRepository.existsByReporterIdAndReportedUserId(reporterId, reportedUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already reported.");
        }

        // Save the report
        Report newReport = new Report(reporter, reported, reason);
        reportRepository.save(newReport);
    }
}
