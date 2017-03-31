package componenttest.custom.junit.runner;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import componenttest.annotation.MinimumJavaLevel;

/**
 * This test is an alternative to {@link SimpleFindServerAndStartItTest} for when there is no server to be found.
 * Deprecated: Use {@link MinimumJavaLevel} instead
 */
@Deprecated
public class NoOpTestForOldJava7 {

    @Test
    public void noOp() {
        assertTrue(true);
    }

}
