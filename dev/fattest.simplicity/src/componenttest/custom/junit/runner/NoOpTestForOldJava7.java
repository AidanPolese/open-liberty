/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.custom.junit.runner;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This test is an alternative to {@link SimpleFindServerAndStartItTest} for when there is no server to be found.
 */
public class NoOpTestForOldJava7 {

    @Test
    public void noOp() {
        assertTrue(true);
    }

}
