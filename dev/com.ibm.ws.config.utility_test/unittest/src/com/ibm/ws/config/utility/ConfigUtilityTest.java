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
package com.ibm.ws.config.utility;

import java.io.PrintStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.ws.config.utility.utils.ConsoleWrapper;
import com.ibm.ws.install.InstallException;

/**
 *
 */
public class ConfigUtilityTest {

    private final Mockery mock = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private final ConsoleWrapper stdin = mock.mock(ConsoleWrapper.class, "stdin");
    private final PrintStream stdout = mock.mock(PrintStream.class, "stdout");
    private final PrintStream stderr = mock.mock(PrintStream.class, "stderr");
    private ConfigUtility util;

    @Before
    public void setUp() {
        util = new ConfigUtility(stdin, stdout, stderr);
    }

    @After
    public void tearDown() {
        mock.assertIsSatisfied();
    }

    /**
     * Verify that any missing I/O streams will not result in catastrophic
     * failure.
     *
     * @throws InstallException
     */
    @Test
    public void nullStdin() throws InstallException {
        mock.checking(new Expectations() {
            {
                one(stderr).println(with(any(String.class)));
            }
        });
        ConfigUtility util = new ConfigUtility(null, stdout, stderr);
        util.runProgram(null);
    }

    /**
     * Verify that a missing Console streams will not result in catastrophic
     * failure.
     *
     * @throws InstallException
     */
    @Test
    public void unavailableConsole() throws InstallException {
        final ConsoleWrapper stdin = mock.mock(ConsoleWrapper.class, "override_stdin");
        mock.checking(new Expectations() {
            {
                allowing(stdin).isInputStreamAvailable();
                will(returnValue(false));
                allowing(stdout).println(with(any(String.class)));
            }
        });
        ConfigUtility util = new ConfigUtility(stdin, stdout, stderr);
        util.runProgram(new String[] {});
    }

    /**
     * Verify that any missing I/O streams will not result in catastrophic
     * failure.
     *
     * @throws InstallException
     */
    @Test
    public void nullStdout() throws InstallException {
        mock.checking(new Expectations() {
            {
                one(stderr).println(with(any(String.class)));
            }
        });
        ConfigUtility util = new ConfigUtility(stdin, null, stderr);
        util.runProgram(null);
    }

    /**
     * Verify that any missing I/O streams will not result in catastrophic
     * failure.
     *
     * @throws InstallException
     */
    @Test
    public void nullStderr() throws InstallException {
        mock.checking(new Expectations() {
            {
                one(stdout).println(with(any(String.class)));
            }
        });
        ConfigUtility util = new ConfigUtility(stdin, stdout, null);
        util.runProgram(null);
    }

    /**
     * Verify that no arguments results in usage. Cover the case
     * where no tasks are registered.
     *
     * @throws InstallException
     */
    @Test
    public void noArgumentsDrivesUsageWithNoTasks() throws InstallException {
        mock.checking(new Expectations() {
            {
                one(stdout).println(with(any(String.class)));
            }
        });

        util.runProgram(new String[] {});
    }

    /**
     * Verify that no arguments results in usage.
     *
     * @throws InstallException
     */
    @Test
    public void noArgumentsDrivesUsageWithKnownTasks() throws InstallException {
        mock.checking(new Expectations() {
            {
                one(stdout).println(with(any(String.class)));
            }
        });

        util.runProgram(new String[] {});
    }

}