/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.custom.junit.runner;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.websphere.simplicity.log.Log;
import componenttest.annotation.FeatureRequiresMinimumJavaLevel;
import componenttest.annotation.MinimumJavaLevel;

/**
 * See also {@link JavaLevelFilter}, {@link MinimumJavaLevel}, and {@link FeatureRequiresMinimumJavaLevel} which is a nicer way
 * of disabling tests.
 */
public class OnlyRunInJava7Rule implements TestRule {
    /** This constant is exposed to any test code to use. It is <code>true</code> iff the FAT is running in Java 7 or higher. */
    public static final boolean IS_JAVA_7_OR_HIGHER = System.getProperty("java.specification.version").matches("1\\.[789]");

    @Override
    public Statement apply(final Statement statement, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (IS_JAVA_7_OR_HIGHER) {
                    statement.evaluate();
                } else {
                    Log.info(description.getTestClass(), description.getMethodName(), "Test class or method is skipped due to Java 7 rule");
                }
            }
        };
    }
}
