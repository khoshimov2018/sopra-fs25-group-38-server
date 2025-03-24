package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.

import java.io.Serializable; 
import java.time.LocalDate; 

@Entity
@Table(name = "REPORT")
public class Report implements Serializable{

    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false)
    private Long reportedUserId;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDate reportDate;
    
    /*
     * Getter & setter
     */

    public Long getId() {
        return id;
    }

    // reporter & reported person ID
    public Long getReporterId() {
        return reporterId;
    }

    public Long getreportedUserId() {
        return reportedUserId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    /*
     * Constructor
     */
    public Report(){
        this.reportDate = LocalDate.now();
    }

    public Report(User reporter, User reportedPerson, String reason){
        this.reporterId = reporter.getId();
        this.reportedUserId = reportedPerson.getId();
        this.reason = reason;
        this.reportDate = LocalDate.now();
    }

}
