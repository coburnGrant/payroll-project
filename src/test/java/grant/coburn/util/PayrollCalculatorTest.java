package grant.coburn.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;

class PayrollCalculatorTest {
    private Employee salariedEmployee;
    private Employee hourlyEmployee;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;

    @BeforeEach
    void setUp() {
        // Create a salaried employee with $52,000 annual salary
        salariedEmployee = new Employee(
            "EMP001",
            "IT",
            "Software Engineer",
            "John",
            "Doe",
            Employee.Status.ACTIVE,
            LocalDate.of(1990, 1, 1),
            LocalDate.of(2020, 1, 1),
            Employee.PayType.SALARY,
            52000.0,
            Employee.MedicalCoverage.FAMILY,
            2
        );

        // Create an hourly employee with $25/hour rate
        hourlyEmployee = new Employee(
            "EMP002",
            "HR",
            "HR Specialist",
            "Jane",
            "Smith",
            Employee.Status.ACTIVE,
            LocalDate.of(1992, 1, 1),
            LocalDate.of(2021, 1, 1),
            Employee.PayType.HOURLY,
            25.0,
            Employee.MedicalCoverage.SINGLE,
            0
        );

        // Set up a two-week pay period
        payPeriodStart = LocalDate.of(2024, 3, 1);
        payPeriodEnd = LocalDate.of(2024, 3, 14);
    }

    @Test
    void testSalariedEmployeeRegularPay() {
        // Salaried employee should get their daily rate * days in period
        PayrollCalculator.PayrollResult result = PayrollCalculator.calculatePayroll(
            salariedEmployee,
            List.of(),
            payPeriodStart,
            payPeriodEnd
        );

        // $52,000 / 365 = $142.46575342 daily rate
        // 14 days * $142.46575342 = $1994.52054795
        assertEquals(1994.52, result.regularPay, 0.1);
        assertEquals(0.0, result.overtimePay, 0.01);
        assertEquals(1994.52, result.grossPay, 0.01);
        
        // Verify deductions
        assertEquals(1994.52 * PayrollCalculator.STATE_TAX_RATE, result.stateTax, 0.01);
        assertEquals(1994.52 * PayrollCalculator.FEDERAL_TAX_RATE, result.federalTax, 0.01);
        assertEquals(1994.52 * PayrollCalculator.SOCIAL_SECURITY_RATE, result.socialSecurityTax, 0.01);
        assertEquals(1994.52 * PayrollCalculator.MEDICARE_RATE, result.medicareTax, 0.01);
        assertEquals(100.0, result.medicalDeduction, 0.01); // Family coverage
        assertEquals(90.0, result.dependentStipend, 0.01); // 2 dependents * $45
    }

    @Test
    void testSalariedEmployeeWithPTO() {
        // Create a PTO time entry for 8 hours
        TimeEntry ptoEntry = new TimeEntry(
            salariedEmployee.getEmployeeId(),
            payPeriodStart,
            8.0,
            true
        );

        PayrollCalculator.PayrollResult result = PayrollCalculator.calculatePayroll(
            salariedEmployee,
            List.of(ptoEntry),
            payPeriodStart,
            payPeriodEnd
        );

        // PTO should not affect salary
        assertEquals(1994.52, result.regularPay, 0.1);
        assertEquals(0.0, result.overtimePay, 0.01);
        assertEquals(1994.52, result.grossPay, 0.01);
    }

    @Test
    void testHourlyEmployeeRegularHours() {
        // Create time entries for regular hours (40 hours)
        List<TimeEntry> timeEntries = Arrays.asList(
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart, 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(1), 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(2), 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(3), 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(4), 8.0, false)
        );

        PayrollCalculator.PayrollResult result = PayrollCalculator.calculatePayroll(
            hourlyEmployee,
            timeEntries,
            payPeriodStart,
            payPeriodEnd
        );

        // 40 hours * $25 = $1,000.00
        double regularPay = 40.0 * 25.0;
        assertEquals(regularPay, result.regularPay, 0.01);
        assertEquals(0.0, result.overtimePay, 0.01);
        assertEquals(regularPay, result.grossPay, 0.01);
    }

    @Test
    void testHourlyEmployeeWithOvertime() {
        // Create time entries with overtime (45 hours)
        List<TimeEntry> timeEntries = Arrays.asList(
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart, 9.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(1), 9.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(2), 9.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(3), 9.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(4), 9.0, false)
        );

        PayrollCalculator.PayrollResult result = PayrollCalculator.calculatePayroll(
            hourlyEmployee,
            timeEntries,
            payPeriodStart,
            payPeriodEnd
        );

        // Regular hours: 40 * $25 = $1,000.00
        double regularPay = 40.0 * 25.0;
        // Overtime hours: 5 * ($25 * 1.5) = $187.50
        double overtimePay = 5.0 * (25.0 * 1.5);
        assertEquals(regularPay, result.regularPay, 0.01);
        assertEquals(overtimePay, result.overtimePay, 0.01);
        assertEquals(regularPay + overtimePay, result.grossPay, 0.01);
    }

    @Test
    void testHourlyEmployeeWithPTO() {
        // Create time entries with PTO (32 regular hours + 8 PTO hours)
        List<TimeEntry> timeEntries = Arrays.asList(
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart, 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(1), 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(2), 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(3), 8.0, false),
            new TimeEntry(hourlyEmployee.getEmployeeId(), payPeriodStart.plusDays(4), 8.0, true)
        );

        PayrollCalculator.PayrollResult result = PayrollCalculator.calculatePayroll(
            hourlyEmployee,
            timeEntries,
            payPeriodStart,
            payPeriodEnd
        );

        // PTO hours are paid at regular rate
        // Regular hours: 40 * $25 = $1,000.00
        double regularPay = 40.0 * 25.0;
        assertEquals(regularPay, result.regularPay, 0.01);
        assertEquals(0.0, result.overtimePay, 0.01);
        assertEquals(regularPay, result.grossPay, 0.01);
    }

    @Test
    void testZeroHoursWorked() {
        // Test hourly employee with no hours worked
        PayrollCalculator.PayrollResult result = PayrollCalculator.calculatePayroll(
            hourlyEmployee,
            List.of(),
            payPeriodStart,
            payPeriodEnd
        );

        assertEquals(0.0, result.regularPay, 0.01);
        assertEquals(0.0, result.overtimePay, 0.01);
        assertEquals(0.0, result.grossPay, 0.01);
        
        // Verify deductions are still calculated
        assertEquals(0.0, result.stateTax, 0.01);
        assertEquals(0.0, result.federalTax, 0.01);
        assertEquals(0.0, result.socialSecurityTax, 0.01);
        assertEquals(0.0, result.medicareTax, 0.01);
        assertEquals(0.0, result.medicalDeduction, 0.01);
        assertEquals(0.0, result.dependentStipend, 0.01);
        assertEquals(0.0, result.netPay, 0.01);
    }

    @Test
    void testPartialPayPeriod() {
        // Test salaried employee with partial pay period (7 days)
        LocalDate partialEnd = payPeriodStart.plusDays(6);
        PayrollCalculator.PayrollResult result = PayrollCalculator.calculatePayroll(
            salariedEmployee,
            List.of(),
            payPeriodStart,
            partialEnd
        );

        // $52,000 / 365 = $142.46575342 daily rate
        double dailyRate = 52_000.0 / 365.0;
        // 7 days * $142.47 = $997.26027394
        double sevenDayPay = 7.0 * dailyRate;
        assertEquals(sevenDayPay, result.regularPay, 0.01);
        assertEquals(0.0, result.overtimePay, 0.01);
        assertEquals(sevenDayPay, result.grossPay, 0.01);
    }
} 