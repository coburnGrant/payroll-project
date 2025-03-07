package grant.coburn.report;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import grant.coburn.dao.PayrollRecordDAO;
import grant.coburn.model.PayrollRecord;

/**
 * Implementation of ReportGenerator for PDF format.
 */
public class PDFReportGenerator implements ReportGenerator {
    private final PayrollRecordDAO payrollRecordDAO;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DeviceRgb ALTERNATE_ROW_COLOR = new DeviceRgb(240, 240, 240);
    private static final float[] COLUMN_WIDTHS = {
        1.0f,  // Employee ID
        1.2f,  // Pay Period Start
        1.2f,  // Pay Period End
        1.0f,  // Gross Pay
        1.0f,  // Net Pay
        0.8f,  // Medical
        0.8f,  // Dependent
        0.8f,  // State Tax
        0.8f,  // Federal Tax
        0.8f,  // SS Tax
        0.8f,  // Medicare
        0.8f,  // Employer SS
        0.8f,  // Employer Med
        0.8f,  // Overtime
        0.8f   // Total Deduct
    };

    private static final String[] COLUMN_HEADERS = {
        "Emp ID",
        "Start Date",
        "End Date",
        "Gross",
        "Net",
        "Medical",
        "Depend",
        "State Tax",
        "Fed Tax",
        "SS Tax",
        "Medicare",
        "Emp SS",
        "Emp Med",
        "OT Pay",
        "Deduct"
    };

    private static final String[] COLUMN_TOOLTIPS = {
        "Employee ID",
        "Pay Period Start Date",
        "Pay Period End Date",
        "Gross Pay",
        "Net Pay",
        "Medical Deduction",
        "Dependent Stipend",
        "State Tax",
        "Federal Tax",
        "Social Security Tax",
        "Medicare Tax",
        "Employer Social Security",
        "Employer Medicare",
        "Overtime Pay",
        "Total Deductions"
    };

    public PDFReportGenerator() {
        this.payrollRecordDAO = PayrollRecordDAO.shared;
    }

    @Override
    public void generateReport(String outputPath, LocalDate startDate, LocalDate endDate) throws IOException {
        List<PayrollRecord> records = payrollRecordDAO.getPayrollRecordsByDateRange(startDate, endDate);

        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4.rotate());
        
        // Add page number handler
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PageNumberHandler());
        
        Document document = new Document(pdf);
        document.setMargins(20, 20, 40, 20); // top, right, bottom, left

        // Add title and date range
        document.add(new Paragraph("Payroll Report")
            .setFontSize(16)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(String.format("Period: %s - %s",
            startDate.format(DATE_FORMATTER),
            endDate.format(DATE_FORMATTER)))
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER));

        // Add summary section
        addSummarySection(document, records);

        // Create table
        Table table = new Table(UnitValue.createPercentArray(COLUMN_WIDTHS))
            .useAllAvailableWidth()
            .setFontSize(8)
            .setMarginTop(20);

        // Add headers
        addTableHeader(table);

        // Add data
        boolean isAlternateRow = false;
        for (PayrollRecord record : records) {
            addTableRow(table, record, isAlternateRow);
            isAlternateRow = !isAlternateRow;
        }

        document.add(table);
        document.close();
    }

    private void addSummarySection(Document document, List<PayrollRecord> records) {
        double totalGrossPay = 0;
        double totalNetPay = 0;
        double totalStateTax = 0;
        double totalFederalTax = 0;
        double totalSSTax = 0;
        double totalMedicareTax = 0;
        double totalEmployerSS = 0;
        double totalEmployerMedicare = 0;

        for (PayrollRecord record : records) {
            totalGrossPay += record.getGrossPay();
            totalNetPay += record.getNetPay();
            totalStateTax += record.getStateTax();
            totalFederalTax += record.getFederalTax();
            totalSSTax += record.getSocialSecurityTax();
            totalMedicareTax += record.getMedicareTax();
            totalEmployerSS += record.getEmployerSocialSecurity();
            totalEmployerMedicare += record.getEmployerMedicare();
        }

        Table summaryTable = new Table(new float[]{2, 1})
            .useAllAvailableWidth()
            .setMarginTop(10)
            .setMarginBottom(20)
            .setFontSize(10);

        addSummaryRow(summaryTable, "Total Records:", String.valueOf(records.size()));
        addSummaryRow(summaryTable, "Total Gross Pay:", formatMoney(totalGrossPay));
        addSummaryRow(summaryTable, "Total Net Pay:", formatMoney(totalNetPay));
        addSummaryRow(summaryTable, "Total State Tax:", formatMoney(totalStateTax));
        addSummaryRow(summaryTable, "Total Federal Tax:", formatMoney(totalFederalTax));
        addSummaryRow(summaryTable, "Total Social Security Tax:", formatMoney(totalSSTax));
        addSummaryRow(summaryTable, "Total Medicare Tax:", formatMoney(totalMedicareTax));
        addSummaryRow(summaryTable, "Total Employer SS:", formatMoney(totalEmployerSS));
        addSummaryRow(summaryTable, "Total Employer Medicare:", formatMoney(totalEmployerMedicare));

        document.add(summaryTable);
    }

    private void addSummaryRow(Table table, String label, String value) {
        table.addCell(new Cell()
            .add(new Paragraph(label))
            .setBold()
            .setBackgroundColor(ALTERNATE_ROW_COLOR)
            .setBorder(null));
        table.addCell(new Cell()
            .add(new Paragraph(value))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBackgroundColor(ALTERNATE_ROW_COLOR)
            .setBorder(null));
    }

    private void addTableHeader(Table table) {
        for (String header : COLUMN_HEADERS) {
            Cell headerCell = new Cell()
                .add(new Paragraph(header)
                    .setFixedLeading(20))
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setHeight(20)
                .setPadding(2);

            table.addHeaderCell(headerCell);
        }
    }

    private void addTableRow(Table table, PayrollRecord record, boolean isAlternate) {
        Cell[] cells = new Cell[]{
            createCell(record.getEmployeeId(), TextAlignment.CENTER),
            createCell(record.getPayPeriodStart().format(DATE_FORMATTER), TextAlignment.CENTER),
            createCell(record.getPayPeriodEnd().format(DATE_FORMATTER), TextAlignment.CENTER),
            createMoneyCell(record.getGrossPay()),
            createMoneyCell(record.getNetPay()),
            createMoneyCell(record.getMedicalDeduction()),
            createMoneyCell(record.getDependentStipend()),
            createMoneyCell(record.getStateTax()),
            createMoneyCell(record.getFederalTax()),
            createMoneyCell(record.getSocialSecurityTax()),
            createMoneyCell(record.getMedicareTax()),
            createMoneyCell(record.getEmployerSocialSecurity()),
            createMoneyCell(record.getEmployerMedicare()),
            createMoneyCell(record.getOvertimePay()),
            createMoneyCell(record.getTotalDeductions())
        };

        for (Cell cell : cells) {
            if (isAlternate) {
                cell.setBackgroundColor(ALTERNATE_ROW_COLOR);
            }
            table.addCell(cell);
        }
    }

    private Cell createCell(String value, TextAlignment alignment) {
        return new Cell()
            .add(new Paragraph(value)
                .setFixedLeading(20))
            .setTextAlignment(alignment)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setHeight(20)
            .setPadding(2);
    }

    private Cell createMoneyCell(double amount) {
        return createCell(formatMoney(amount), TextAlignment.RIGHT)
            .setPaddingRight(5);
    }

    private String formatMoney(double amount) {
        return String.format("$%,.2f", amount);
    }

    private static class PageNumberHandler implements IEventHandler {
        private static final String COPYRIGHT = "© " + LocalDate.now().getYear() + " Payroll System - Grant Coburn";

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdf.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();
            
            PdfCanvas canvas = new PdfCanvas(page);
            // Add page number in center
            canvas.beginText()
                .setFontAndSize(pdf.getDefaultFont(), 9)
                .moveText(pageSize.getWidth() / 2, 20)
                .showText(String.format("Page %d of %d", pageNumber, pdf.getNumberOfPages()))
                .endText();

            // Add copyright in bottom left
            canvas.beginText()
                .setFontAndSize(pdf.getDefaultFont(), 8)
                .moveText(20, 20)
                .showText(COPYRIGHT)
                .endText();
        }
    }
} 