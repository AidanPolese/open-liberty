package componenttest.custom.junit.runner;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.websphere.simplicity.log.Log;

import componenttest.annotation.MinimumJavaLevel;
import componenttest.topology.impl.JavaInfo;

/**
 * Deprecated: use {@link MinimumJavaLevel} instead
 */
@Deprecated
public class OnlyRunInJava7Rule implements TestRule {
    /** This constant is exposed to any test code to use. It is <code>true</code> iff the FAT is running in Java 7 or higher. */
    public static final boolean IS_JAVA_7_OR_HIGHER = JavaInfo.JAVA_VERSION >= 7;

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
