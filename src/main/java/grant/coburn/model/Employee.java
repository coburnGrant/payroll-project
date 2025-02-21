package grant.coburn.model;

import java.time.LocalDate;

public class Employee {
    private String employeeId;
    private String userId;
    private String department;
    private String jobTitle;
    private String firstName;
    private String lastName;
    private Status status;
    private LocalDate dateOfBirth;
    private Gender gender;
    private PayType payType;
    private String companyEmail;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private String picturePath;
    private LocalDate hireDate;
    private Double baseSalary;
    private MedicalCoverage medicalCoverage;
    private Integer dependentsCount;

    public enum Status {
        ACTIVE, TERMINATED;
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum PayType {
        SALARY, HOURLY
    }

    public enum MedicalCoverage {
        SINGLE, FAMILY
    }

    // Constructor
    public Employee(
        String employeeId, 
        String department,
        String jobTitle,
        String firstName, 
        String lastName, 
        Status status, 
        LocalDate dateOfBirth,
        LocalDate hireDate, 
        PayType payType,
        Double baseSalary, 
        MedicalCoverage medicalCoverage, 
        Integer dependentsCount
    ) {
        this.employeeId = employeeId;
        this.department = department;
        this.jobTitle = jobTitle;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.dateOfBirth = dateOfBirth;
        this.hireDate = hireDate;
        this.payType = payType;
        this.baseSalary = baseSalary;
        this.medicalCoverage = medicalCoverage;
        this.dependentsCount = dependentsCount;
    }

    // Getters and setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public PayType getPayType() { return payType; }
    public void setPayType(PayType payType) { this.payType = payType; }
    
    public Double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(Double baseSalary) { this.baseSalary = baseSalary; }
    
    public MedicalCoverage getMedicalCoverage() { return medicalCoverage; }
    public void setMedicalCoverage(MedicalCoverage medicalCoverage) { this.medicalCoverage = medicalCoverage; }
    
    public Integer getDependentsCount() { return dependentsCount; }
    public void setDependentsCount(Integer dependentsCount) { this.dependentsCount = dependentsCount; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getPicturePath() { return picturePath; }
    public void setPicturePath(String picturePath) { this.picturePath = picturePath; }
} 