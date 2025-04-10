package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterIdAndReportedUserId(Long reporterId, Long reportedUserId);

    List<Report> findAllByReportedUserId(Long reportedUserId);

    List<Report> findAllByReporterId(Long reporterId);

    // Delete all records involving the given user(both of reporterId, reportedUserId)
    void deleteAllByReporterIdOrReportedUserId(Long reporterId, Long reportedUserId);
}
