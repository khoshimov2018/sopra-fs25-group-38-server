package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class ReportDTO {
    private Long reporterId;
    private Long reportedId;
    private String reason;

    // Default constructor required for JSON deserialization and mapping frameworks
    public ReportDTO() {}

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getReportedId() {
        return reportedId;
    }

    public void setReportedId(Long reportedId) {
        this.reportedId = reportedId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
