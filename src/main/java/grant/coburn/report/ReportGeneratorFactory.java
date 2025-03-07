package grant.coburn.report;

/**
 * Factory class for creating report generators.
 */
public class ReportGeneratorFactory {
    /**
     * Creates a report generator for the specified format.
     *
     * @param format The format of the report to generate
     * @return A report generator for the specified format
     */
    public static ReportGenerator createGenerator(ReportFormat format) {
        switch (format) {
            case CSV:
                return new CSVReportGenerator();
            case PDF:
                return new PDFReportGenerator();
            default:
                throw new IllegalArgumentException("Unsupported report format: " + format);
        }
    }
} 