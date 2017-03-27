/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

public class PackageRunnableTest {
    private static String serverName = "runnableTestServer";
    private static LibertyServer server = LibertyServerFactory.getLibertyServer(serverName);
    private static final File runnableJar = new File("publish/" + serverName + ".jar");
    private static final File extractDirectory = new File("publish" + File.separator + "wlpExtract");

    /*
     * return env as array and add WLP_JAR_EXTRACT_DIR=extractDirectory
     */
    private static String[] runEnv(String extractDirectory) {

        Map<String, String> envmap = System.getenv();
        Iterator<String> iKeys = envmap.keySet().iterator();
        String[] envArray = new String[envmap.size() + 1];

        int i = 0;
        while (iKeys.hasNext()) {
            String key = iKeys.next();
            String val = envmap.get(key);
            envArray[i++] = key + "=" + val;
        }
        // add extract dir to end 
        String extDirVar = "WLP_JAR_EXTRACT_DIR=" + extractDirectory;
        envArray[envArray.length - 1] = extDirVar;

        return envArray;

    }

    @BeforeClass
    public static void setupClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Test
    public void testRunnableJar() throws Exception {

        // Doesn't work on z/OS (because you can't package into a jar on z/OS)
        assumeTrue(!System.getProperty("os.name").equals("z/OS"));

        String stdout = server.executeServerScript("package",
                                                   new String[] { "--archive=" + runnableJar.getAbsolutePath(),
                                                                 "--include=minify,runnable" }).getStdout();

        String searchString = "Server " + serverName + " package complete";
        if (!stdout.contains(searchString)) {
            System.out.println("Warning: test case " + PackageRunnableTest.class.getName() + " could not package server " + serverName);
            return; // get out 
        }

        if (!extractDirectory.exists()) {
            extractDirectory.mkdirs();
        }

        assertTrue("Extract directory " + extractDirectory.getAbsolutePath() + " does not exist.", extractDirectory.exists());

        String cmd = "java -jar " + runnableJar.getAbsolutePath();
        Process proc = Runtime.getRuntime().exec(cmd, runEnv(extractDirectory.getAbsolutePath()), null); // run server

        // setup and start reader threads for error and output streams 
        StreamReader errorReader = new StreamReader(proc.getErrorStream(), "ERROR", null);
        errorReader.start();
        StreamReader outputReader = new StreamReader(proc.getInputStream(), "OUTPUT", "CWWKF0011I");
        outputReader.start();

        int count = 0;

        // wait up to 90 seconds to find watch for string

        boolean found = outputReader.foundWatchFor();
        while (!found && count <= 90) {

            synchronized (proc) {
                proc.wait(1000); // wait 1 second 
                System.out.println("Waiting for server to complete initialization - " + count + " seconds elapsed.");
            }
            found = outputReader.foundWatchFor();
            count++;
        }

        assertTrue("Server did not start successfully in time.", found);

        proc.destroy(); // ensure no process left behind

    }

    class StreamReader extends Thread
    {
        InputStream is;
        String type;
        OutputStream os;
        String watchFor;
        boolean foundWatchFor = false;

        StreamReader(InputStream is, String type, String watchFor)
        {
            this(is, type, watchFor, null);
        }

        StreamReader(OutputStream os, String type, String watchFor)
        {
            this.os = os;
            this.type = type;
            this.watchFor = watchFor;

        }

        StreamReader(InputStream is, String type, String watchFor, OutputStream redirect)
        {
            this.is = is;
            this.type = type;
            this.os = redirect;
            this.watchFor = watchFor;
        }

        public boolean foundWatchFor() {
            return foundWatchFor;
        }

        @Override
        public void run()
        {
            try {
                // stdin, process stream is output
                if (type.equals("INPUT")) {
                    runOutputStream();
                }
                // else stdout, stderr, process stream is input 
                else {
                    runInputStream();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        public void runInputStream() throws IOException {
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);

            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
            {
                if (pw != null)
                    pw.println(line);
                System.out.println(line);
                // if watchFor string supplied - search for it 
                if (watchFor != null) {
                    if (line.indexOf(watchFor) > -1) {
                        foundWatchFor = true;
                    }
                }

            }

            if (pw != null)
                pw.flush();
        }

        public void runOutputStream() throws IOException {
            OutputStreamWriter osr = new OutputStreamWriter(os, "UTF-8");
            BufferedWriter br = new BufferedWriter(osr);
            br.write("Y");
        }
    }

}
