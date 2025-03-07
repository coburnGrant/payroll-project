package grant.coburn.view.admin;

import java.io.File;
import java.time.LocalDate;

import grant.coburn.report.ReportFormat;
import grant.coburn.report.ReportGenerator;
import grant.coburn.report.ReportGeneratorFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * View for generating payroll reports.
 */
public class GenerateReportView extends VBox {
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private final ComboBox<ReportFormat> formatComboBox;

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
        formatComboBox = new ComboBox<>();
        formatComboBox.getItems().addAll(ReportFormat.values());
        formatComboBox.setValue(ReportFormat.PDF);
        
        HBox formatBox = new HBox(10);
        formatBox.setAlignment(Pos.CENTER);
        formatBox.getChildren().addAll(
            new Label("Report Format:"), 
            formatComboBox
        );

        // Generate button
        Button generateButton = new Button("Generate Report");
        generateButton.setOnAction(e -> generateReport());

        getChildren().addAll(
            titleLabel,
            dateBox,
            formatBox,
            generateButton
        );
    }

    private void generateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        ReportFormat format = formatComboBox.getValue();

        if (startDate == null || endDate == null) {
            showError("Please select both start and end dates");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showError("End date cannot be before start date");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("payroll_report_" + 
            startDate.toString() + "_to_" + endDate.toString() + 
            format.getFileExtension());
        
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
            format.getDisplayName(),
            "*" + format.getFileExtension()
        ));

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                ReportGenerator generator = ReportGeneratorFactory.createGenerator(format);
                generator.generateReport(file.getAbsolutePath(), startDate, endDate);
                showSuccess("Report generated successfully!");
            } catch (Exception e) {
                showError("Failed to generate report: " + e.getMessage());
            }
        }
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