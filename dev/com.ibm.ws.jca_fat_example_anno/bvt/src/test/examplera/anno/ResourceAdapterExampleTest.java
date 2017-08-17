package test.examplera.anno;

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

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.SharedOutputManager;

public class ResourceAdapterExampleTest {
    private static SharedOutputManager outputMgr;

    private static final String PORT = System.getProperty("HTTP_default", "8000");

    /**
     * Utility method to run a test on RAExampleServlet.
     * 
     * @param query query string for the servlet
     * @return output of the servlet
     * @throws IOException if an error occurs
     */
    private StringBuilder runInServlet(String query) throws IOException {
        URL url = new URL("http://localhost:" + PORT + "/ExampleApp?" + query);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("GET");

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String sep = System.getProperty("line.separator");
            StringBuilder lines = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine())
                lines.append(line).append(sep);

            if (lines.indexOf("ERROR:") >= 0)
                fail("Error in servlet output: " + lines);

            return lines;
        } finally {
            con.disconnect();
        }
    }

    /**
     * Capture stdout/stderr output to the manager.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // There are variations of this constructor: 
        // e.g. to specify a log location or an enabled trace spec. Ctrl-Space for suggestions
        outputMgr = SharedOutputManager.getInstance();
        outputMgr.captureStreams();
    }

    /**
     * Final teardown work when class is exiting.
     * 
     * @throws Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Make stdout and stderr "normal"
        outputMgr.restoreStreams();
    }

    /**
     * Individual teardown after each test.
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        // Clear the output generated after each method invocation
        outputMgr.resetStreams();
    }

    @Test
    public void testAddAndFind() throws Exception {
        StringBuilder output;

        // attempt find for an entry that isn't in the table
        output = runInServlet("functionName=FIND&capital=Saint%20Paul");
        if (output.indexOf("Did not FIND any entries") < 0)
            throw new Exception("Entry should not have been found. Output: " + output);

        // add
        output = runInServlet("functionName=ADD&state=Iowa&population=30741869&area=56272&capital=Des%20Moines");
        output = runInServlet("functionName=ADD&state=Minnesota&population=5379139&area=86939&capital=Saint%20Paul");

        // find
        output = runInServlet("functionName=FIND&capital=Saint%20Paul");
        if (output.indexOf("Successfully performed FIND with output: {area=86939, capital=Saint Paul, population=5379139, state=Minnesota}") < 0)
            throw new Exception("Did not find entry. Output: " + output);
    }

    @Test
    public void testAddAndRemove() throws Exception {
        StringBuilder output;

        // add
        output = runInServlet("functionName=ADD&city=Rochester&state=Minnesota&population=106769");
        output = runInServlet("functionName=ADD&city=Stewartville&state=Minnesota&population=5916");
        output = runInServlet("functionName=ADD&city=Byron&state=Minnesota&population=4914");

        // remove
        output = runInServlet("functionName=REMOVE&city=Stewartville");
        if (output.indexOf("Successfully performed REMOVE with output: {city=Stewartville, population=5916, state=Minnesota}") < 0)
            throw new Exception("Did not report entry removed. Output: " + output);

        // attempt removal of something that doesn't exist
        output = runInServlet("functionName=REMOVE&city=Stewartville");
        if (output.indexOf("Successfully performed REMOVE") >= 0)
            throw new Exception("Entry should not have been present to remove. Output: " + output);
    }

    @Test
    public void testMessageDrivenBean() throws Exception {
        StringBuilder output = runInServlet("functionName=ADD&county=Olmsted&state=Minnesota&population=147066&area=654.5");
        if (output.indexOf("Successfully performed ADD with output: {area=654.5, county=Olmsted, population=147066, state=Minnesota}") < 0)
            throw new Exception("Did not report entry added. Output: " + output);

        // search messages log for MDB output
        boolean found = false;
        BufferedReader log = new BufferedReader(new FileReader(System.getProperty("server.root") + "/logs/messages.log"));
        try {
            for (String line = log.readLine(); line != null && !found; line = log.readLine())
                found = line.contains("ExampleMessageDrivenBean.onMessage record = {area=654.5, county=Olmsted, population=147066, state=Minnesota}");
        } finally {
            log.close();
        }

        if (!found)
            throw new Exception("Output from message driven bean not found in messages.log");
    }
}
