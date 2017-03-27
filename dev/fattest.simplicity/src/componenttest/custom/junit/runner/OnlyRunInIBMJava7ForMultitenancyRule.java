/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.custom.junit.runner;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import componenttest.topology.utils.JavaMTUtils;

/**
 * Rule to test the all the criteria for running the FATs in multitenancy mode.
 */
public class OnlyRunInIBMJava7ForMultitenancyRule implements TestRule {
    private static final Class<?> c = OnlyRunInIBMJava7ForMultitenancyRule.class;

    @Override
    public Statement apply(Statement statement, Description arg1) {
        return new Java7MutlitenancyStatement(statement);
    }

    private static class Java7MutlitenancyStatement extends Statement {

        private final Statement statement;

        public Java7MutlitenancyStatement(Statement statement) {
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {

            if (JavaMTUtils.checkSupportedEnvForMultiTenancy())
                statement.evaluate(); // Run the test
            else {
                // Skip the test
            }
        }

    }
}
