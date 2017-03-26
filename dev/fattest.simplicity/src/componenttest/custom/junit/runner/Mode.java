/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package componenttest.custom.junit.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for expressing that a test method should only run in a given framework mode.
 * Unannotated tests default to lite, so the the default for an annotated
 * test that hasn't specified mode=something will be full
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Mode {

    /**
     * The test modes in order from least complete, to fullest set of tests
     * Use uppercase, since we toUpperCase on the value passed in for the framework
     */
    public static enum TestMode {
        //RAPID, - could be easily added later for example
        LITE,
        FULL,
        QUARANTINE,
        EXPERIMENTAL
    }

    TestMode value() default TestMode.FULL;
}
