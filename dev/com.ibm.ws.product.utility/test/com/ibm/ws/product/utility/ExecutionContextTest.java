/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.product.utility;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import junit.framework.Assert;

public class ExecutionContextTest {

    private final Mockery mock = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final CommandConsole commandConsole = mock.mock(CommandConsole.class, "commandConsole");

    private final CommandTaskRegistry commandTaskRegistry = new CommandTaskRegistry();

    @Test
    public void testParsingOptions() throws Exception {
        String[] arguments = { "--help", "--output=a.txt", "--encode=us", "abc" };
        ExecutionContext executionContext = createExecutionContext(arguments);
        Assert.assertEquals("The expected option value of --output should be a.txt", "a.txt", executionContext.getOptionValue("--output"));
        Assert.assertEquals("The expected option value of --encode should be us", "us", executionContext.getOptionValue("--encode"));

        Assert.assertTrue("The --help option should exist in the parsed result", executionContext.optionExists("--help"));
        Assert.assertTrue("The abc option should exist in the parsed result", executionContext.optionExists("abc"));

        Assert.assertFalse("The --INVALID should not exist in the parsed result", executionContext.optionExists("--INVALID"));
    }

    @Test
    public void testEmptyParsingOptions() throws Exception {
        String[] arguments = {};
        ExecutionContext executionContext = createExecutionContext(arguments);
        Assert.assertFalse("The --INVALID should not be existing in the parsed result", executionContext.optionExists("--INVALID"));
        Assert.assertFalse("The abc should not be existing in the parsed result", executionContext.optionExists("abc"));
    }

    private ExecutionContext createExecutionContext(String[] args) {
        return new ExecutionContextImpl(commandConsole, args, commandTaskRegistry);
    }

}
