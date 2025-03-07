package grant.coburn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a payroll processing operation.
 * Contains information about success/failure and any validation errors or warnings.
 */
public class PayrollProcessingResult {
    private final boolean success;
    private final List<String> errors;
    private final List<String> warnings;
    private final int employeesProcessed;
    private final int employeesWithErrors;

    private PayrollProcessingResult(Builder builder) {
        this.success = builder.success;
        this.errors = Collections.unmodifiableList(new ArrayList<>(builder.errors));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(builder.warnings));
        this.employeesProcessed = builder.employeesProcessed;
        this.employeesWithErrors = builder.employeesWithErrors;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public int getEmployeesProcessed() {
        return employeesProcessed;
    }

    public int getEmployeesWithErrors() {
        return employeesWithErrors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public static class Builder {
        private boolean success = true;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        private int employeesProcessed = 0;
        private int employeesWithErrors = 0;

        public Builder addError(String error) {
            this.errors.add(error);
            this.success = false;
            return this;
        }

        public Builder addWarning(String warning) {
            this.warnings.add(warning);
            return this;
        }

        public Builder setEmployeesProcessed(int count) {
            this.employeesProcessed = count;
            return this;
        }

        public Builder incrementEmployeesWithErrors() {
            this.employeesWithErrors++;
            return this;
        }

        public PayrollProcessingResult build() {
            return new PayrollProcessingResult(this);
        }
    }
} 