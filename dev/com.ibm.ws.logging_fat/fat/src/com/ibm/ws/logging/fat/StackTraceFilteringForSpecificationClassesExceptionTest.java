/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.fat;

import org.junit.BeforeClass;
import org.junit.Test;

import componenttest.topology.impl.LibertyServerFactory;

/**
 * A test which makes sure internal classes exported as spec-type API don't get filtered
 * from the top of stack traces, but do get filtered from the middle.
 * We want to see exceptions like
 *
 * java.lang.NullPointerException
 * at javax.servlet.http.Cookie.isToken(Cookie.java:384)
 * at javax.servlet.http.Cookie.<init>(Cookie.java:124)
 * at com.ibm.ws.logging.fat.servlet.SpecUsingServlet.doGet(SpecUsingServlet.java:40)
 * at javax.servlet.http.HttpServlet.service(HttpServlet.java:575)
 * at javax.servlet.http.HttpServlet.service(HttpServlet.java:668)
 * at com.ibm.ws.webcontainer.servlet.ServletWrapper.service(ServletWrapper.java:1240)
 * at [internal classes]
 */
public class StackTraceFilteringForSpecificationClassesExceptionTest extends AbstractStackTraceFilteringTest {

    private static final String MAIN_EXCEPTION = "NullPointerException";

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory.getLibertyServer("com.ibm.ws.logging.brokenserver");

        server.startServer();
        server.addInstalledAppForValidation("broken-servlet");
        hitWebPage("broken-servlet", "SpecUsingServlet", true);
    }

    @Test
    public void testConsoleIsTrimmedForUserUseOfSpecificationClass() throws Exception {
        assertConsoleLogContains("The console log should at the very least have our exception in it.", MAIN_EXCEPTION);
        // How many stack traces we get depends a bit on server internals, so to try and be more robust,
        // count how many [ERROR] lines we get and match this
        // We don't want to count errors that don't have stack traces, so try and exclude these by also checking
        // for message id 'SRVE.*E'. This still isn't totally robust since it won't catch printed Errors
        // if the message doesn't include the id 'SRVE.*E' or misspells it, as our current messages do
        int errorCount = server.findStringsInFileInLibertyServerRoot("ERROR.*SRVE.*E", CONSOLE_LOG).size();
        int causedByCount = server.findStringsInFileInLibertyServerRoot("Caused by", CONSOLE_LOG).size();
        // Sanity check - we got an [ERROR], right?
        assertConsoleLogContains("The console log should have [ERROR] prefix in it", "ERROR");

        assertConsoleLogCountEquals("The console stack should only have one [internal classes] in it per stack trace.",
                                    INTERNAL_CLASSES_REGEXP, errorCount);
        // The javax.servlet methods shouldn't be stripped out, because they're spec used by the app
        final int servletFrames = 9;
        assertConsoleLogCountEquals("The console log should have several frames from the specification javax.servlet classes", "javax.servlet", servletFrames);

        assertConsoleLogContains("The console log should have the user class in it", "SpecUsingServlet");

        // We want one line of internal WAS classes in the console
        assertConsoleLogCountEquals("There should be exactly one IBM frame per stack trace",
                                    "at com.ibm.ws.webcontainer", errorCount + causedByCount);

    }

}
