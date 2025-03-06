package grant.coburn.model;

import java.time.LocalDate;

public class TimeEntry {
    private Long entryId;
    private String employeeId;
    private LocalDate workDate;
    private Double hoursWorked;
    private boolean isPto;
    private boolean isLocked;  // True when payroll is submitted
    private Double overtimeHours;
    private Double regularHours;

    public TimeEntry(String employeeId, LocalDate workDate, Double hoursWorked, boolean isPto) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.isPto = isPto;
        this.isLocked = false;
        calculateHours();
    }

    private void calculateHours() {
        if (isPto) {
            this.regularHours = hoursWorked;
            this.overtimeHours = 0.0;
        } else {
            // All non-PTO hours are regular hours
            this.regularHours = hoursWorked;
            this.overtimeHours = 0.0;
        }
    }

    // Getters and setters
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { 
        this.workDate = workDate;
        calculateHours();
    }
    
    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { 
        this.hoursWorked = hoursWorked;
        calculateHours();
    }
    
    public boolean isPto() { return isPto; }
    public void setPto(boolean isPto) { 
        this.isPto = isPto;
        calculateHours();
    }
    
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean isLocked) { this.isLocked = isLocked; }

    public Double getOvertimeHours() { return overtimeHours; }
    public Double getRegularHours() { return regularHours; }
} 