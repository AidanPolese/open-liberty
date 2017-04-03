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

import java.lang.reflect.Method;

import org.junit.Test;

import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode.TestMode;

/**
 * Test that will pass in all test modes on all platforms with any JDK.
 *
 * Intended for use in test buckets where all other tests may be filtered out
 * for some test modes or environments (such as only for Java 7). Since the build
 * requires at lest one passing test, this provides a simple way to insure one
 * test is not filtered, and always reports passing.
 */
public class AlwaysPassesTest {

    static final String ALWAYS_PASSES_METHOD_NAME = "testThatWillAlwaysPass";

    @Test
    @Mode(TestMode.LITE)
    @MinimumJavaLevel(javaLevel = 1.6)
    public void testThatWillAlwaysPass() throws Exception {}

    static Method getAlwaysPassesMethod() {
        try {
            return AlwaysPassesTest.class.getMethod(ALWAYS_PASSES_METHOD_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
