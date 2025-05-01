package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class ReportDTO {
    private Long reporterId;
    private Long reportedId;
    private String reason;

    /**
     * Default constructor is required for frameworks like Jackson
     * that use reflection to instantiate DTOs during deserialization.
     * This constructor intentionally contains no logic.
     */
    public ReportDTO() {
        // Intentionally left blank
    }

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
