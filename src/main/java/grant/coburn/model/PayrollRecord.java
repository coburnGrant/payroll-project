package grant.coburn.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PayrollRecord {
    private Long recordId;
    private String employeeId;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private double grossPay;
    private double netPay;
    private double medicalDeduction;
    private double dependentStipend;
    private double stateTax;
    private double federalTax;
    private double socialSecurityTax;
    private double medicareTax;
    private double employerSocialSecurity;
    private double employerMedicare;
    private double overtimePay;
    private LocalDateTime creationDate;

    public PayrollRecord(
        String employeeId,
        LocalDate payPeriodStart,
        LocalDate payPeriodEnd,
        double grossPay,
        double netPay,
        double medicalDeduction,
        double dependentStipend,
        double stateTax,
        double federalTax,
        double socialSecurityTax,
        double medicareTax,
        double employerSocialSecurity,
        double employerMedicare
    ) {
        this.employeeId = employeeId;
        this.payPeriodStart = payPeriodStart;
        this.payPeriodEnd = payPeriodEnd;
        this.grossPay = grossPay;
        this.netPay = netPay;
        this.medicalDeduction = medicalDeduction;
        this.dependentStipend = dependentStipend;
        this.stateTax = stateTax;
        this.federalTax = federalTax;
        this.socialSecurityTax = socialSecurityTax;
        this.medicareTax = medicareTax;
        this.employerSocialSecurity = employerSocialSecurity;
        this.employerMedicare = employerMedicare;
        this.creationDate = LocalDateTime.now();
    }

    // Getters and setters
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public LocalDate getPayPeriodStart() { return payPeriodStart; }
    public void setPayPeriodStart(LocalDate payPeriodStart) { this.payPeriodStart = payPeriodStart; }
    
    public LocalDate getPayPeriodEnd() { return payPeriodEnd; }
    public void setPayPeriodEnd(LocalDate payPeriodEnd) { this.payPeriodEnd = payPeriodEnd; }
    
    public double getGrossPay() { return grossPay; }
    public void setGrossPay(double grossPay) { this.grossPay = grossPay; }
    
    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }
    
    public double getMedicalDeduction() { return medicalDeduction; }
    public void setMedicalDeduction(double medicalDeduction) { this.medicalDeduction = medicalDeduction; }
    
    public double getDependentStipend() { return dependentStipend; }
    public void setDependentStipend(double dependentStipend) { this.dependentStipend = dependentStipend; }
    
    public double getStateTax() { return stateTax; }
    public void setStateTax(double stateTax) { this.stateTax = stateTax; }
    
    public double getFederalTax() { return federalTax; }
    public void setFederalTax(double federalTax) { this.federalTax = federalTax; }
    
    public double getSocialSecurityTax() { return socialSecurityTax; }
    public void setSocialSecurityTax(double socialSecurityTax) { this.socialSecurityTax = socialSecurityTax; }
    
    public double getMedicareTax() { return medicareTax; }
    public void setMedicareTax(double medicareTax) { this.medicareTax = medicareTax; }
    
    public double getEmployerSocialSecurity() { return employerSocialSecurity; }
    public void setEmployerSocialSecurity(double employerSocialSecurity) { this.employerSocialSecurity = employerSocialSecurity; }
    
    public double getEmployerMedicare() { return employerMedicare; }
    public void setEmployerMedicare(double employerMedicare) { this.employerMedicare = employerMedicare; }

    public double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(double overtimePay) { this.overtimePay = overtimePay; }
    
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
} 