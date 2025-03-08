classDiagram
direction BT
class AdminDashboardView {
  + AdminDashboardView(User, Stage) 
  - Runnable onLogout
  - handleReports() void
  - handleLogout() void
  - createActionCard(String, String, String, EventHandler~ActionEvent~) VBox
  - handlePayroll() void
  - setupUI() void
  - showEmployeeManagement() void
   Runnable onLogout
}
class App {
  + App() 
  - handleCreateAccount(User) Boolean
  + main(String[]) void
  - showChangePasswordView(User, boolean) void
  + start(Stage) void
  - handleLogin(String, String) User
  - showEmployeeDashboard(User, Employee) void
  - showCreateAccountView() void
  - showDashboard(User) void
  - showAdminDashboard(User) void
  - createScene(Parent, double, double) Scene
  - showLoginView() void
}
class Builder {
  + Builder() 
  - int employeesProcessed
  + addWarning(String) Builder
  + build() PayrollProcessingResult
  + addError(String) Builder
  + incrementEmployeesWithErrors() Builder
   int employeesProcessed
}
class CSVReportGenerator {
  + CSVReportGenerator() 
  - formatMoney(double) String
  + generateReport(String, LocalDate, LocalDate) void
}
class ChangePasswordView {
  + ChangePasswordView(User, boolean) 
  - Runnable onSuccess
  - Runnable onBack
  - showError(String) void
  - handleSave() void
  - handleBack() void
  - showSuccess(String) void
  - addFormField(GridPane, String, Control, int) void
  - setupUI() void
   Runnable onBack
   Runnable onSuccess
}
class CreateAccountView {
  + CreateAccountView() 
  - Runnable onBackToLogin
  - Function~User, Boolean~ onCreateAccount
  - clearFields() void
  - setupUI() void
  - showError(String) void
  - validateInput() boolean
  - showSuccess(String) void
  - handleCreateAccount() void
   Function~User, Boolean~ onCreateAccount
   Runnable onBackToLogin
}
class DBProperties {
  + DBProperties() 
}
class DatabaseUtil {
  - DatabaseUtil() 
  - Connection connection
  + getSqlUrl(String, int, String) String
  + closeConnection() void
   Connection connection
}
class Employee {
  + Employee(String, String, String, String, String, Status, LocalDate, LocalDate, PayType, Double, MedicalCoverage, Integer) 
  - String addressLine1
  - String firstName
  - Status status
  - String lastName
  - String zip
  - String jobTitle
  - PayType payType
  - LocalDate hireDate
  - String employeeId
  - Gender gender
  - String addressLine2
  - String department
  - Double baseSalary
  - Integer dependentsCount
  - LocalDate dateOfBirth
  - String picturePath
  - MedicalCoverage medicalCoverage
  - String companyEmail
  - String city
  - String state
   LocalDate dateOfBirth
   String zip
   Gender gender
   String addressLine1
   String state
   String picturePath
   String companyEmail
   Double baseSalary
   String addressLine2
   String department
   MedicalCoverage medicalCoverage
   String lastName
   String city
   String fullName
   String firstName
   String jobTitle
   PayType payType
   String employeeId
   LocalDate hireDate
   Status status
   Integer dependentsCount
}
class EmployeeCredentials {
  + EmployeeCredentials(String, String, String) 
}
class EmployeeDAO {
  + EmployeeDAO(DatabaseUtil) 
  - generateSecurePassword(boolean) String
  + createEmployee(Employee, boolean) EmployeeCredentials
  - createEmployeeFromResultSet(ResultSet) Employee
  + deleteEmployee(String, boolean) boolean
  + deleteEmployee(String) boolean
  - generateEmployeeId() String
  + getEmployee(String) Employee
  - hardDeleteEmployee(String) boolean
  - setEmployeeParameters(PreparedStatement, Employee) void
  - createTestEmployee(String, String, String, String, PayType, double, String, Gender, String, String, String, String, String, MedicalCoverage, int) Employee
  - testEmployees() Employee[]
  + insertTestEmployees() void
  + updateEmployee(Employee) boolean
  + createEmployee(Employee) EmployeeCredentials
  - softDeleteEmployee(String) boolean
  + getEmployeeById(String) Employee
   String userAsAdmin
   List~Employee~ allEmployees
}
class EmployeeDashboardView {
  + EmployeeDashboardView(User, Employee, Stage) 
  - Runnable onLogout
  - showError(String) void
  - createDetailLabel(String, String) HBox
  - handlePaycheck() void
  - setupUI() void
  - handleLogout() void
  - handleTimeEntry() void
  - handleTimeSheet() void
  - createActionCard(String, String, String, EventHandler~ActionEvent~) VBox
   Runnable onLogout
}
class EmployeeFormView {
  + EmployeeFormView(Employee, Consumer~Employee~, Runnable) 
  - handleSave() void
  - validateInput() boolean
  - setupUI() void
  - handleCancel() void
  - updateSalaryLabel() void
  - addFormField(GridPane, String, Control, int) void
  - populateFields() void
  - showError(String) void
}
class EmployeeManagementView {
  + EmployeeManagementView(Stage) 
  - Runnable onBack
  - handleAddTestEmployees() void
  - showCredentialsAlert(EmployeeCredentials) void
  - loadEmployees() void
  - setupTableSelection() void
  - handleEditEmployee() void
  - setupTable() void
  - confirmDelete(String, String) boolean
  - setupUI() void
  - viewSelectedEmployeeTimeSheet() void
  - processDeleteEmployee(String, boolean) void
  - saveNewEmployee(Employee) void
  - updateEmployee(Employee) void
  - handleAddEmployee() void
  - handleDeleteEmployee() void
  - handleBack() void
  - showAlert(String, String) void
  - filterEmployees(String) void
   Runnable onBack
}
class EmployeePaychecksView {
  + EmployeePaychecksView(Employee) 
  - showPaycheckDetails(PayrollRecord) void
  - setupUI() void
  - setupTable() void
  - loadPaychecks() void
}
class Gender {
<<enumeration>>
  + Gender() 
  + values() Gender[]
  + valueOf(String) Gender
}
class GenerateReportView {
  + GenerateReportView() 
  - showError(String) void
  - generateReport() void
  - showSuccess(String) void
   List~ReportFormat~ selectedFormats
}
class LabeledTextField {
  + LabeledTextField(String) 
  + clearText() void
  + setNumbersOnly() void
   String text
   double doubleValue
}
class LoginView {
  + LoginView() 
  - BiFunction~String, String, User~ onLogin
  - Runnable onCreateAccountClick
  - showError(String) void
  - handleLogin() void
  - setupUI() void
   Runnable onCreateAccountClick
   BiFunction~String, String, User~ onLogin
}
class MedicalCoverage {
<<enumeration>>
  + MedicalCoverage() 
  + values() MedicalCoverage[]
  + valueOf(String) MedicalCoverage
}
class MonetaryRoundingVisitor {
  + MonetaryRoundingVisitor() 
  - roundToCents(double) double
  + visit(PayrollResult) void
}
class MonetaryRoundingVisitorTest {
  + MonetaryRoundingVisitorTest() 
  ~ testRoundingUp() void
  ~ testRoundingDown() void
  ~ testExactValues() void
}
class PDFReportGenerator {
  + PDFReportGenerator() 
  - formatMoney(double) String
  - addSummaryRow(Table, String, String) void
  + generateReport(String, LocalDate, LocalDate) void
  - addHeader(Document, LocalDate, LocalDate) void
  - addCell(Table, String, boolean) void
  - addTableHeaders(Table) void
  - createTableHeaderCell(String) Cell
  - addSummarySection(Document, List~PayrollRecord~) void
}
class PageNumberHandler {
  + PageNumberHandler() 
  + handleEvent(Event) void
}
class PasswordUtil {
  + PasswordUtil() 
  + main(String[]) void
  + checkPasswordMD5(String, String) boolean
  + encryptPasswordMD5(String) String
  + bcryptCheckPassword(String, String) boolean
  + bcryptPassword(String) String
  + isValidPassword(String) boolean
}
class PasswordUtilTest {
  + PasswordUtilTest() 
  + testNullPassword() void
  + testValidPassword() void
  + testPasswordWithoutLowerCase() void
  + testPasswordWithoutSpecialChar() void
  + testPasswordWithoutUpperCase() void
  + testPasswordWithSpaces() void
  + testPasswordHashing() void
  + testEmptyPassword() void
  + testPasswordTooShort() void
  + testPasswordWithoutNumber() void
}
class PasswordValidationException {
  + PasswordValidationException(String) 
}
class PayType {
<<enumeration>>
  + PayType() 
  + valueOf(String) PayType
  + values() PayType[]
}
class PaycheckView {
  + PaycheckView(Stage, Employee, PayrollRecord, Runnable) 
  - createDetailRow(String, String) HBox
  - setupUI(Employee, PayrollRecord) void
  - formatMoney(double) String
}
class PayrollCalculator {
  + PayrollCalculator() 
  - determineSalaryRegularPay(double, LocalDate, LocalDate) double
  + calculatePayrollPreview(Employee, List~TimeEntry~) PayrollResult
  + calculatePayroll(Employee, List~TimeEntry~, LocalDate, LocalDate) PayrollResult
  - determineHourlyRegularPay(double, List~TimeEntry~, PayrollResult) void
  - determineMedicalDeduction(Employee) double
  - calculateDeductionsAndNetPay(PayrollResult, Employee) void
  - validatePayrollResult(PayrollResult) void
}
class PayrollCalculatorTest {
  + PayrollCalculatorTest() 
  ~ testHourlyEmployeeDeductions() void
  ~ setUp() void
  ~ testSalariedEmployeeWithPTO() void
  ~ testZeroHoursWorked() void
  ~ testPartialPayPeriod() void
  ~ testSalariedEmployeeRegularPay() void
  ~ testHourlyEmployeeRegularHours() void
  ~ testHourlyEmployeeWithOvertime() void
  ~ testHourlyEmployeeWithPTO() void
}
class PayrollProcessingResult {
  - PayrollProcessingResult(Builder) 
  - List~String~ errors
  - boolean success
  - int employeesProcessed
  - List~String~ warnings
  - int employeesWithErrors
  + hasWarnings() boolean
  + hasErrors() boolean
   int employeesProcessed
   boolean success
   int employeesWithErrors
   List~String~ warnings
   List~String~ errors
}
class PayrollProcessingView {
  + PayrollProcessingView(Stage) 
  - Runnable onBack
  - refreshPayrollTable() void
  - showErrorDialog(String) void
  - showSuccess(String) void
  - setupPayrollTable() void
  - showError(String) void
  - handleDeleteRecord() void
  - showSuccessDialog(String) void
  - handleViewPaycheck() void
  - handleProcessPayroll() void
  - setupUI() void
   Runnable onBack
}
class PayrollProcessor {
  - PayrollProcessor() 
  + getPayrollRecords(LocalDate, LocalDate) List~PayrollRecord~
  - lockTimeEntries(List~TimeEntry~) void
  + shared() PayrollProcessor
  + getEmployeeById(String) Employee
  + processPayroll(LocalDate, LocalDate) PayrollProcessingResult
  + deletePayrollRecord(String, LocalDate, LocalDate) boolean
   List~Employee~ allEmployees
}
class PayrollRecord {
  + PayrollRecord(String, LocalDate, LocalDate, double, double, double, double, double, double, double, double, double, double) 
  - Long recordId
  - double medicareTax
  - double stateTax
  - String employeeId
  - double overtimePay
  - double netPay
  - LocalDate payPeriodStart
  - double socialSecurityTax
  - double dependentStipend
  - LocalDate payPeriodEnd
  - double grossPay
  - double employerMedicare
  - double federalTax
  - double medicalDeduction
  - double employerSocialSecurity
  - LocalDateTime creationDate
   double medicareTax
   double medicalDeduction
   LocalDate payPeriodEnd
   double dependentStipend
   double totalDeductions
   Long recordId
   double stateTax
   double socialSecurityTax
   double netPay
   LocalDate payPeriodStart
   double grossPay
   double overtimePay
   LocalDateTime creationDate
   double employerMedicare
   String employeeId
   double federalTax
   double employerSocialSecurity
}
class PayrollRecordDAO {
  - PayrollRecordDAO(DatabaseUtil) 
  + getLatestPayrollRecord(String) PayrollRecord
  + savePayrollRecord(PayrollRecord) boolean
  + getPayrollRecordsByDateRange(LocalDate, LocalDate) List~PayrollRecord~
  - createPayrollRecordFromResultSet(ResultSet) PayrollRecord
  + getPayrollRecordsByEmployee(String) List~PayrollRecord~
  + deletePayrollRecord(String, LocalDate, LocalDate) boolean
}
class PayrollResult {
  + PayrollResult() 
  + accept(PayrollResultVisitor) void
}
class PayrollResultValidationVisitor {
  + PayrollResultValidationVisitor() 
  - StringBuilder errors
  - validateNonNegative(double, String) void
  + hasErrors() boolean
  - validateTaxRate(double, double, double, String) void
  + visit(PayrollResult) void
   String errors
}
class PayrollResultValidationVisitorTest {
  + PayrollResultValidationVisitorTest() 
  ~ testInvalidGrossPay() void
  ~ testMismatchedEmployerTaxes() void
  ~ testInvalidTaxRates() void
  ~ testMultipleErrors() void
  ~ testInvalidPayPeriodDates() void
  ~ testInvalidNetPay() void
  ~ testValidPayrollResult() void
  ~ setUp() void
  ~ testNegativeValues() void
  ~ testZeroGrossPay() void
}
class PayrollResultVisitor {
<<Interface>>
  + visit(PayrollResult) void
}
class ReportFormat {
<<enumeration>>
  - ReportFormat(String, String) 
  - String displayName
  - String fileExtension
  + values() ReportFormat[]
  + valueOf(String) ReportFormat
  + toString() String
   String fileExtension
   String displayName
}
class ReportGenerator {
<<Interface>>
  + generateReport(String, LocalDate, LocalDate) void
}
class ReportGeneratorFactory {
  + ReportGeneratorFactory() 
  + createGenerator(ReportFormat) ReportGenerator
}
class Status {
<<enumeration>>
  + Status() 
  + values() Status[]
  + valueOf(String) Status
}
class TimeEntry {
  + TimeEntry(String, LocalDate, Double, boolean) 
  - Double regularHours
  - Double overtimeHours
  - Double hoursWorked
  - boolean isPto
  - Long entryId
  - LocalDate workDate
  - boolean isLocked
  - String employeeId
  - calculateHours() void
   Double hoursWorked
   Double overtimeHours
   LocalDate workDate
   boolean isPto
   Long entryId
   boolean isLocked
   Double regularHours
   String employeeId
}
class TimeEntryDAO {
  - TimeEntryDAO(DatabaseUtil) 
  + getTimeEntryByEmployeeIdAndDate(String, LocalDate) TimeEntry
  - extractTimeEntryFromResultSet(ResultSet) TimeEntry
  + deleteTimeEntry(TimeEntry) boolean
  + getTimeEntriesByEmployeeId(String) List~TimeEntry~
  + saveTimeEntry(TimeEntry) boolean
  + getTimeEntriesByEmployeeIdAndDateRange(String, LocalDate, LocalDate) List~TimeEntry~
  + updateTimeEntry(TimeEntry) boolean
}
class TimeEntryEditView {
  + TimeEntryEditView(Employee, TimeEntry) 
  - Runnable onSave
  - Runnable onCancel
  - addFormField(GridPane, String, Control, int) void
  - handleSave() void
  - showError(String) void
  - setupUI() void
  - updateCalculations() void
   Runnable onSave
   Runnable onCancel
}
class TimeEntryView {
  + TimeEntryView(Employee) 
  - Runnable onBack
  - Runnable onSave
  - handleBack() void
  - showSuccess(String) void
  - showError(String) void
  - formatPercent(double) String
  - handleSave() void
  - setupUI() void
  - addFormField(GridPane, String, Control, int) void
  - updateCalculations() void
   Runnable onBack
   Runnable onSave
}
class TimeSheetView {
  + TimeSheetView(Stage, Employee, Runnable) 
  - setupUI() void
  + show() void
  - createTimeEntryTable() TableView~TimeEntry~
}
class TimeSheetView {
  + TimeSheetView(Employee, Stage) 
  - Runnable onBack
  - setupTimeEntryTable() void
  - handleDelete() void
  - loadTimeEntries() void
  - setupUI() void
  - handleAddTimeEntry() void
  - showError(String) void
  - handleEdit() void
  - showSuccess(String) void
   Runnable onBack
}
class User {
  + User(String, String, UserType, String) 
  - String password
  - String employeeId
  - UserType userType
  - String userId
  - String email
   String password
   UserType userType
   String email
   String employeeId
   String userId
}
class UserDAO {
  + UserDAO(DatabaseUtil) 
  + deleteAllUsers() boolean
  + authenticateUser(String, String) User
  + createUser(User, Connection) boolean
  + mustChangePassword(String) boolean
  + deleteUser(String) boolean
  + changePassword(String, String, String) boolean
  + createUser(User) boolean
}
class UserType {
<<enumeration>>
  + UserType() 
  + values() UserType[]
  + valueOf(String) UserType
}

PayrollProcessingResult  -->  Builder 
CSVReportGenerator  ..>  ReportGenerator 
EmployeeDAO  -->  EmployeeCredentials 
Employee  -->  Gender 
Employee  -->  MedicalCoverage 
MonetaryRoundingVisitor  ..>  PayrollResultVisitor 
PDFReportGenerator  ..>  ReportGenerator 
PDFReportGenerator  -->  PageNumberHandler 
Employee  -->  PayType 
PayrollCalculator  -->  PayrollResult 
PayrollResultValidationVisitor  ..>  PayrollResultVisitor 
Employee  -->  Status 
User  -->  UserType 
