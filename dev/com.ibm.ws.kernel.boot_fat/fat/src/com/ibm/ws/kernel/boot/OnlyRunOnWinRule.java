/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.websphere.simplicity.log.Log;

/**
 * Rule to run on Windows.
 */
public class OnlyRunOnWinRule implements TestRule {

    /** This constant is exposed to any test code to use. It is <code>true</code> iff the FAT is running on z/OS. */
    public static final boolean IS_RUNNING_ON_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

    @Override
    public Statement apply(final Statement statement, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (IS_RUNNING_ON_WINDOWS) {
                    statement.evaluate();
                } else {
                    Log.info(description.getTestClass(), description.getMethodName(), "Test class or method is skipped due to run on Windows rule");
                }
            }
        };
    }
}
