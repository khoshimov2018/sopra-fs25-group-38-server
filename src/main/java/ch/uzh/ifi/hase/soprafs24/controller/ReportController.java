package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.ReportDTO;
import ch.uzh.ifi.hase.soprafs24.service.ReportBlockService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportBlockService reportBlockService;

    public ReportController(ReportBlockService reportBlockService) {
        this.reportBlockService = reportBlockService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createReport(@RequestBody ReportDTO reportDTO) {
        reportBlockService.reportUser(reportDTO.getReporterId(), reportDTO.getReportedId(), reportDTO.getReason());
    }
}
