package grant.coburn.view.admin;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import grant.coburn.report.ReportFormat;
import grant.coburn.report.ReportGenerator;
import grant.coburn.report.ReportGeneratorFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * View for generating payroll reports.
 */
public class GenerateReportView extends VBox {
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private final VBox formatSelectionBox;
    private final List<CheckBox> formatCheckBoxes;

    public GenerateReportView() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20));
        setSpacing(10);

        // Title
        Label titleLabel = new Label("Generate Payroll Report");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Date range selection
        startDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        endDatePicker = new DatePicker(LocalDate.now());
        
        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.getChildren().addAll(
            new Label("Start Date:"), startDatePicker,
            new Label("End Date:"), endDatePicker
        );

        // Format selection
        formatSelectionBox = new VBox(5); // 5px spacing
        formatSelectionBox.setPadding(new Insets(5));
        formatCheckBoxes = new ArrayList<>();
        
        for (ReportFormat format : ReportFormat.values()) {
            CheckBox cb = new CheckBox(format.toString());
            if (format == ReportFormat.PDF) {
                cb.setSelected(true); // Default selection
            }
            formatCheckBoxes.add(cb);
            formatSelectionBox.getChildren().add(cb);
        }
        
        // Generate button
        Button generateButton = new Button("Generate Reports");
        generateButton.setOnAction(e -> generateReport());

        getChildren().addAll(
            titleLabel,
            dateBox,
            formatSelectionBox,
            generateButton
        );
    }

    private void generateReport() {
        List<ReportFormat> selectedFormats = getSelectedFormats();
        if (selectedFormats.isEmpty()) {
            showError("Please select at least one report format");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showError("Please select both start and end dates");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showError("End date cannot be before start date");
            return;
        }

        // Let user choose the output directory
        javafx.stage.DirectoryChooser dirChooser = new javafx.stage.DirectoryChooser();
        dirChooser.setTitle("Choose Output Directory");
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Downloads"));
        File outputDir = dirChooser.showDialog(getScene().getWindow());
        
        if (outputDir == null) {
            return; // User cancelled
        }

        List<String> generatedFiles = new ArrayList<>();
        try {
            for (ReportFormat format : selectedFormats) {
                String baseFileName = "payroll_report_" + 
                    startDate.toString() + "_to_" + endDate.toString();
                String filePath = outputDir.getAbsolutePath() + 
                    File.separator + baseFileName + 
                    format.getFileExtension();

                ReportGenerator generator = ReportGeneratorFactory.createGenerator(format);
                generator.generateReport(filePath, startDate, endDate);
                generatedFiles.add(filePath);
            }
            
            // Show success message with all generated file paths
            StringBuilder message = new StringBuilder("Reports generated successfully!\nLocations:\n");
            for (String path : generatedFiles) {
                message.append(path).append("\n");
            }
            showSuccess(message.toString());
        } catch (Exception e) {
            showError("Failed to generate report: " + e.getMessage());
        }
    }

    private List<ReportFormat> getSelectedFormats() {
        List<ReportFormat> selectedFormats = new ArrayList<>();
        for (int i = 0; i < formatCheckBoxes.size(); i++) {
            if (formatCheckBoxes.get(i).isSelected()) {
                selectedFormats.add(ReportFormat.values()[i]);
            }
        }
        return selectedFormats;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 