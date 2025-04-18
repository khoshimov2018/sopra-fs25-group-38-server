package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Report;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ReportDTO;
import ch.uzh.ifi.hase.soprafs24.service.ReportBlockService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportBlockService reportBlockService;
    private final UserService userService;

    public ReportController(ReportBlockService reportBlockService, UserService userService) {
        this.reportBlockService = reportBlockService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createReport(@RequestBody ReportDTO reportDTO) {
        reportBlockService.reportUser(reportDTO.getReporterId(), reportDTO.getReportedId(), reportDTO.getReason());
    }

    @GetMapping
    public List<ReportDTO> getAllReports(@RequestHeader("Authorization") String token) {
        if (!userService.isAdmin(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view reports");
        }

        List<Report> reports = reportBlockService.getAllReports();

        return reports.stream()
                .map(report -> {
                    ReportDTO dto = new ReportDTO();
                    dto.setReporterId(report.getReporterId());
                    dto.setReportedId(report.getreportedUserId());
                    dto.setReason(report.getReason());
                    return dto;
                })
                .toList();
    }
}
