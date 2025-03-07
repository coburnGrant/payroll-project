package grant.coburn.util;

import grant.coburn.util.PayrollCalculator.PayrollResult;

public interface PayrollResultVisitor {
    void visit(PayrollResult result);
} 