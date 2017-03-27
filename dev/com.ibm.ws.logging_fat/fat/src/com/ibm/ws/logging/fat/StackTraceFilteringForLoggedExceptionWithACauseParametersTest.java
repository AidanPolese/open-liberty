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
package com.ibm.ws.logging.fat;

import org.junit.BeforeClass;
import org.junit.Test;

import componenttest.topology.impl.LibertyServerFactory;

public class StackTraceFilteringForLoggedExceptionWithACauseParametersTest extends AbstractStackTraceFilteringTest {

    private static final String EXCEPTION_WITH_A_CAUSE = "BrokenWithACauseException";
    private static final String CAUSE_EXCEPTION = "ReasonItAllWentWrongException";
    private static final String CAUSED_BY = "Caused by:";

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory
                        .getLibertyServer("com.ibm.ws.logging.brokenserver");

        server.startServer();

        //Make sure the application has come up before proceeding
        server.addInstalledAppForValidation("broken-servlet");

        // Hit the servlet, to drive the error
        hitWebPage("broken-servlet", "BrokenWithACauseServlet", true);

    }

    @Test
    public void testConsoleIsTrimmedForLoggedParameter() throws Exception {
        assertConsoleLogContains("The console log did not have our exception in it at all.",
                                 EXCEPTION_WITH_A_CAUSE);
        assertConsoleLogContains("The console stack was not trimmed.",
                                 INTERNAL_CLASSES_REGEXP);
        // We better have a line for the class that threw the exception
        assertConsoleLogContains("The console stack was trimmed too aggressively and stripped out our servlet.",
                                 "at com.ibm.ws.logging.fat.servlet.BrokenWithACauseServlet.doGet");

        assertConsoleLogContains("The console log should say 'Caused by'", CAUSED_BY);
        assertConsoleLogContains("The console log include the root cause", CAUSE_EXCEPTION);

    }

    @Test
    public void testMessagesIsNotTrimmedForLoggedParameter() throws Exception {
        assertMessagesLogContains("The messages log did not have our exception in it at all.",
                                  EXCEPTION_WITH_A_CAUSE);
        assertMessagesLogDoesNotContain("The messages log had a trimmed stack trace in it.", INTERNAL_CLASSES_REGEXP);
        // We don't want to be seeing anything that looks like internal WAS classes in the console
        assertMessagesLogContains("The messages stack was apparently untrimmed, but it didn't have the internal WAS class stacks we expected in it",
                                  "at com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest");
        assertConsoleLogContains("The console log should say 'Caused by'", CAUSED_BY);
        assertConsoleLogContains("The console log include the root cause", CAUSE_EXCEPTION);
    }

}
