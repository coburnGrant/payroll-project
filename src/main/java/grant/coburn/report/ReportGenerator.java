package grant.coburn.report;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Interface defining the contract for generating payroll reports.
 */
public interface ReportGenerator {
    /**
     * Generates a report for the specified date range and saves it to the given path.
     *
     * @param outputPath The path where the report should be saved
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @throws IOException If there is an error writing the report
     */
    void generateReport(String outputPath, LocalDate startDate, LocalDate endDate) throws IOException;
} 