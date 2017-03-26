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
package componenttest.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import componenttest.annotation.IgnoreTestNamesRule;
import componenttest.custom.junit.runner.FeatureDoesNotStartOnLowJavaVersionsTestStub;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

/**
 * Run all tests with their method names using {@link FATServletClient}.
 */
class RunFatClientUsingTestNamesRule implements TestRule {
    private final LibertyServer server;
    private final String path;

    public RunFatClientUsingTestNamesRule(final LibertyServer server, final String path) {
        this.server = server;
        this.path = path;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        if (!description.getMethodName().equals(FeatureDoesNotStartOnLowJavaVersionsTestStub.SYNTHETIC_METHOD_NAME)
            && description.getAnnotation(IgnoreTestNamesRule.class) == null) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    final String testName = description.getMethodName();
                    FATServletClient.runTest(server, path, testName);
                }
            };
        } else {
            return base;
        }
    }
}