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
package com.ibm.ws.fat.hpel.tests;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.fat.VerboseTestCase;
import com.ibm.ws.fat.hpel.setup.HpelSetup;
import com.ibm.ws.fat.ras.util.CommonTasks;
import componenttest.topology.utils.HttpUtils;

/**
 * This FAT test is written against RTC defect 137645 and 99171
 */
public class TraceSpecificationSetToAllTest extends VerboseTestCase {

//    private static LibertyServer server = LibertyServerFactory.getLibertyServer("HpelServer");
    private ApplicationServer server = null;
    private static final int CONN_TIMEOUT = 60;
    private final Class<?> c = TraceSpecificationSetToAllTest.class;

    RemoteFile rProfRootDir = null;
    RemoteFile rProfBinFile = null;

    private static final String traceSpecification = "com.ibm.ws.logging.*=all";

    public TraceSpecificationSetToAllTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        server = HpelSetup.getServerUnderTest();

        if (!CommonTasks.isHpelEnabled(server)) {
            // HPEL is not enabled.
            this.logStep("HPEL is not enabled on " + server.getName() + ", attempting to enable.");
            CommonTasks.setHpelEnabled(HpelSetup.getServerUnderTest(), true);
            // if HPEL was not enabled, make sure trace spec is not valid to ensure restart below.
            this.logStepCompleted();
        }

        CommonTasks.setHpelTraceSpec(HpelSetup.getServerUnderTest(), traceSpecification);

        CommonTasks.addBootstrapProperty(server, "com.ibm.ws.logging.trace.specification", "com.ibm.ws.logging.*=all:com.ibm.ws.org.*=all=enabled");

        this.logStep("Bouncing server for new spec to take effect. Stopping application server");
        HpelSetup.getServerUnderTest().stop();
        this.logStepCompleted();

        // Start Server
        this.logStep("Starting the application server");
        HpelSetup.getServerUnderTest().start();
        this.logStepCompleted();

        this.logStep("Checking the trace spec post app server restart: "
                     + CommonTasks.getHpelTraceSpec(HpelSetup.getServerUnderTest()));
        this.logStepCompleted();
        assertTrue("Failed assertion that HPEL trace specification is set to " + traceSpecification,
                   traceSpecification.contains(CommonTasks.getHpelTraceSpec(HpelSetup.getServerUnderTest())));
    }

    /**
     * This fat test ensures that if we set trace specification as "com.ibm.ws.logging.trace.specification=*=all"
     * and write the entry, exit level messages then servlet request goes well and
     * server does not throw StackOverflow exception.
     * 
     * RTC WI : 137645
     * 
     * @throws Exception
     */
    public void testResourceInjectionWorkNotGivingStackOverflow() throws Exception {
        String arg1 = "view";
        String compareString;

        String txt = "http://" + server.getBackend().getHostname() + ":" + server.getBackend().getHttpDefaultPort() + "/HpelFat/WritingCustomLogServlet";
        URL url = new URL(txt);
        Log.info(c, "testResourceInjectionWorkNotGivingStackOverflow", "Calling customLogger Application with URL=" + url.toString());

        HttpURLConnection con = HttpUtils.getHttpConnection(url, HttpURLConnection.HTTP_OK, CONN_TIMEOUT);
        BufferedReader br = HttpUtils.getConnectionStream(con);
        String line = br.readLine();
        assertTrue("The response did not contain \'Servlet successfullly completed\' it's content was: " + line,
                   line.contains("Servlet successfullly completed"));

        compareString = "StackOverFlow";
        ProgramOutput lvPrgmOut = exeBinaryLog(new String[] { arg1, server.getBackend().getServerName(), "--includeInstance=latest" });
        Log.info(c, "testResourceInjectionWorkNotGivingStackOverflow", "Server Name : " + server.getBackend().getServerName());

        String out = lvPrgmOut.getStdout();
        assertFalse(compareString, out.contains(compareString));

        /**
         * This assert verify RTC WI : 99171
         * Defect says that HPEL logs does not matches with text log. And comparison string used was
         * "Event:org.osgi.framework.BundleEvent[source=com.ibm.ws.logging.osgi"
         * We have replicated the same scenario here.
         */
        compareString = "BundleEvent STARTING";
        assertTrue(compareString, out.contains(compareString));

        compareString = "Event:org.osgi.framework.BundleEvent[source=com.ibm.ws.logging.osgi";
        assertTrue(compareString, out.contains(compareString));
    }

    /**
     * A method to drive the execution of the binaryLog tool on the remote machine/server that is being tested.
     * 
     * @throws Exception
     */
    private ProgramOutput exeBinaryLog(String[] cmdLineOptions) throws Exception {
        // make platform agnostic to handle .sh and .bat
        String exeExt = "";
        final String BINARY_LOG = "binaryLog";

        if (HpelSetup.getNodeUnderTest().getMachine().getOperatingSystem().equals(OperatingSystem.WINDOWS)) {
            exeExt = ".bat";
        }
        rProfRootDir = new RemoteFile(HpelSetup.getNodeUnderTest().getMachine(), HpelSetup.getServerUnderTest().getBackend().getInstallRoot());
        rProfBinFile = new RemoteFile(HpelSetup.getNodeUnderTest().getMachine(), rProfRootDir, "bin");
        StringBuilder cmd = new StringBuilder(100);
        cmd.append(rProfBinFile.getAbsolutePath()).append(HpelSetup.getNodeUnderTest().getMachine().getOperatingSystem().getFileSeparator());
        cmd.append(BINARY_LOG).append(exeExt).append(" ");

        for (String cmdOption : cmdLineOptions) {
            if (!cmdOption.isEmpty()) {
                cmd.append("\"" + cmdOption + "\" ");
            }
        }

        Log.info(c, "exeBinaryLog", "executing: " + cmd.toString());
        return HpelSetup.getNodeUnderTest().getMachine().execute(cmd.toString(), rProfBinFile.getAbsolutePath());
    }
}
