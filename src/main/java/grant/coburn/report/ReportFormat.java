package grant.coburn.report;

/**
 * Enum defining the available report formats.
 */
public enum ReportFormat {
    CSV("CSV Report", ".csv"),
    PDF("PDF Report", ".pdf");

    private final String displayName;
    private final String fileExtension;

    ReportFormat(String displayName, String fileExtension) {
        this.displayName = displayName;
        this.fileExtension = fileExtension;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 