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

public class StackTraceFilteringForNoClassDefFoundErrorTest extends AbstractStackTraceFilteringTest {

    private static final String MAIN_EXCEPTION = "NoClassDefFoundError";
    private static final String NESTED_EXCEPTION = "ClassNotFoundException";
    private static final String CAUSED_BY = "Caused by:";

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory
                        .getLibertyServer("com.ibm.ws.logging.missingfeatureserver");

        server.startServer();

        //Make sure the application has come up before proceeding
        server.addInstalledAppForValidation("missing-feature-servlet");

        hitWebPage("missing-feature-servlet", "MissingEntityManagerServlet", true);
    }

    @Test
    public void testConsoleIsTrimmedForNoClassDefFoundError() throws Exception {
        assertConsoleLogContains("The console log did not have our exception in it at all.",
                                 MAIN_EXCEPTION);
        assertConsoleLogContains("The console stack was not trimmed.",
                                 INTERNAL_CLASSES_REGEXP);
        // We better have a line for the class that threw the exception, but don't make too many assumptions about what class that was
        assertConsoleLogContains("The console stack didn't show the originating class.",
                                 "at com.ibm.ws.logging");
        // We only want one line of WAS context
        assertConsoleLogCountEquals("The console stack was apparently trimmed, but internal WAS classes got left in it",
                                       "at com.ibm.ws.webcontainer", 1);

    }

    @Test
    public void testRedundantCauseIsStrippedOutForNoClassDefFoundError() throws Exception {
        assertConsoleLogContains("The console log should always have our exception in it.",
                                 MAIN_EXCEPTION);
        assertConsoleLogContains("The console log should have a line saying trimming happened.", INTERNAL_CLASSES_REGEXP);
        assertConsoleLogDoesNotContain("The console log should not have anything about \"Caused by\"",
                                       CAUSED_BY);
        assertConsoleLogDoesNotContain("The console log should not have our nested exception in it.",
                                       NESTED_EXCEPTION);
    }

    @Test
    public void testMessagesIsNotTrimmedForNoClassDefFoundError() throws Exception {
        assertMessagesLogContains("The messages log should have our exception in it.",
                                  MAIN_EXCEPTION);
        assertMessagesLogDoesNotContain("The messages log should not have a trimmed stack trace in it.", INTERNAL_CLASSES_REGEXP);
        assertMessagesLogContains("The messages log should have a 'Caused by' line in it.",
                                  CAUSED_BY);
        assertMessagesLogContains("The message log should have our nested exception in it.",
                                  NESTED_EXCEPTION);
    }

    @Test
    public void testTraceIsNotTrimmedForNoClassDefFoundError() throws Exception {
        assertTraceLogContains("The trace log should not have our exception in it.",
                               MAIN_EXCEPTION);
        assertTraceLogDoesNotContain("The trace log had a trimmed stack trace in it.", INTERNAL_CLASSES_REGEXP);
        assertTraceLogContains("The trace log should have a 'Caused by' line in it.",
                               CAUSED_BY);
        assertTraceLogContains("The trace log should not have our nested exception in it.",
                               NESTED_EXCEPTION);
    }
}
