package com.ibm.ws.zos.logging.zfat;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2016
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import test.common.zos.ZosOperationsFat;

import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.log.Log;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * 
 */
public class ZosLoggingTest {

    /**
     * Default server.
     */
    private static LibertyServer server = LibertyServerFactory.getLibertyServer("defaultServer");

    /**
     * z/OS Operations utility class instance.
     */
    private static ZosOperationsFat zosOperationsFat = new ZosOperationsFat();

    /**
     * Default server job name.
     */
    private static final String SERVER_JOBNAME_DEFAULT = "BBGZSRV";

    /**
     * Custom server job name.
     */
    private static final String SERVER_JOBNAME_CUSTOM1 = "BBGZSRV1";

    /**
     * Log helper.
     */
    private static void log(String method, String msg) {
        Log.info(ZosLoggingTest.class, method, msg);
    }

    /**
     * Pre-FAT execution setup.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        log("setUp", "Entry.");

        // Try to stop/cancel any active jobs left around by this or other FATs.
        stopProcess(SERVER_JOBNAME_DEFAULT, 1, true);
        stopProcess(SERVER_JOBNAME_CUSTOM1, 1, true);
        server.stopServer();

        log("setUp", "Exit.");
    }

    /**
     * 
     */
    @Test
    public void testStartedTaskProc() throws Exception {

        String prevJobId = zosOperationsFat.getMostRecentJobId(SERVER_JOBNAME_DEFAULT);

        zosOperationsFat.startWASProcess(SERVER_JOBNAME_DEFAULT);

        try {
            String newJobId = zosOperationsFat.waitForNewJobId(SERVER_JOBNAME_DEFAULT, prevJobId, 30);

            zosOperationsFat.waitForStringInJobLog(newJobId,
                                                   "CWWKF0011I: The server defaultServer is ready to run a smarter planet",
                                                   30);

            stopProcess(SERVER_JOBNAME_DEFAULT, 30, false);

            List<String> joblog = ZosOperationsFat.getJoblogRaw(newJobId);

            assertNotNull("Joblog is null; job must have failed to start", joblog);

            log("testStartedTaskProc", "joblog: ");
            for (String s : joblog) {
                log("testStartedTaskProc", s);
            }

            // These are routed to hardcopy by LoggingWtoLogHandler 
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKE0001I: The server defaultServer has been launched.", joblog));
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKF0011I: The server defaultServer is ready to run a smarter planet.", joblog));
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKB0001I: Stop command received for server defaultServer.", joblog));

            // These are routed to hardcopy by LoggingHardcopyLogHandler
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0001I: Application userRegistry started", joblog));
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0009I: The application userRegistry has stopped successfully.", joblog));

            // Issued to STDOUT
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0001I: The server defaultServer has been launched", joblog));
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0036I: The server defaultServer stopped after", joblog));
        } finally {
            stopProcess(SERVER_JOBNAME_DEFAULT, 5, true);
        }
    }

    /**
     * 
     */
    @Test
    public void testSubmittedJob() throws Exception {

        String prevJobId = zosOperationsFat.getMostRecentJobId(SERVER_JOBNAME_CUSTOM1);

        ProgramOutput po = server.getMachine().execute("/bin/submit",
                                                       new String[] { EbcdicUtils.convertToEbcdic(server, "bbgzsrv1.jcl") },
                                                       server.getServerRoot(),
                                                       null);

        try {
            log("testSubmittedJob", "JCL submit: RC: " + po.getReturnCode());
            log("testSubmittedJob", "JCL submit: stdout:\n" + po.getStdout());
            log("testSubmittedJob", "JCL submit: stderr:\n" + po.getStderr());

            assertEquals(0, po.getReturnCode());

            String newJobId = zosOperationsFat.waitForNewJobId(SERVER_JOBNAME_CUSTOM1, prevJobId, 30);

            zosOperationsFat.waitForStringInJobLog(newJobId,
                                                   "CWWKF0011I: The server defaultServer is ready to run a smarter planet",
                                                   30);

            stopProcess(SERVER_JOBNAME_CUSTOM1, 30, false);

            List<String> joblog = ZosOperationsFat.getJoblogRaw(newJobId);

            assertNotNull("Joblog is null; job must have failed to start", joblog);

            log("testSubmittedJob", "joblog: ");
            for (String s : joblog) {
                log("testSubmittedJob", s);
            }

            // These are routed to hardcopy by LoggingHardcopyLogHandler
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0001I: Application userRegistry started", joblog));
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0009I: The application userRegistry has stopped successfully.", joblog));

            // Issued to STDOUT
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0001I: The server defaultServer has been launched", joblog));
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0036I: The server defaultServer stopped after", joblog));
        } finally {
            stopProcess(SERVER_JOBNAME_CUSTOM1, 5, true);
        }
    }

    /**
     * 
     */
    @Test
    public void testMsgLog() throws Exception {

        String prevJobId = zosOperationsFat.getMostRecentJobId(SERVER_JOBNAME_CUSTOM1);

        ProgramOutput po = server.getMachine().execute("/bin/submit",
                                                       new String[] { EbcdicUtils.convertToEbcdic(server, "bbgzsrv1.msglog.jcl") },
                                                       server.getServerRoot(),
                                                       null);

        try {
            log("testMsgLog", "JCL submit: RC: " + po.getReturnCode());
            log("testMsgLog", "JCL submit: stdout:\n" + po.getStdout());
            log("testMsgLog", "JCL submit: stderr:\n" + po.getStderr());

            assertEquals(0, po.getReturnCode());

            String newJobId = zosOperationsFat.waitForNewJobId(SERVER_JOBNAME_CUSTOM1, prevJobId, 30);

            zosOperationsFat.waitForStringInJobLog(newJobId,
                                                   "CWWKF0011I: The server defaultServer is ready to run a smarter planet",
                                                   30);

            stopProcess(SERVER_JOBNAME_CUSTOM1, 30, false);

            List<String> joblog = ZosOperationsFat.getJoblogRaw(newJobId);

            assertNotNull("Joblog is null; job must have failed to start", joblog);

            log("testMsgLog", "joblog: ");
            for (String s : joblog) {
                log("testMsgLog", s);
            }

            // These are routed to hardcopy by LoggingHardcopyLogHandler
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0001I: Application userRegistry started", joblog));
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0009I: The application userRegistry has stopped successfully.", joblog));

            // Issued to STDOUT
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0001I: The server defaultServer has been launched", joblog));
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0036I: The server defaultServer stopped after", joblog));

            // MsgLogLogHandler messages
            assertTrue(zosOperationsFat.isStringInLog(" TRAS0018I: The trace state has been changed.", joblog));
            assertTrue(zosOperationsFat.isStringInLog(" CWWKB0104I: Authorized service group ", joblog));
        } finally {
            stopProcess(SERVER_JOBNAME_CUSTOM1, 5, true);
        }
    }

    /**
     * 
     */
    @Test
    public void testMsgLogSegmented() throws Exception {

        String prevJobId = zosOperationsFat.getMostRecentJobId(SERVER_JOBNAME_CUSTOM1);

        ProgramOutput po = server.getMachine().execute("/bin/submit",
                                                       new String[] { EbcdicUtils.convertToEbcdic(server, "bbgzsrv1.msglog.segment.jcl") },
                                                       server.getServerRoot(),
                                                       null);

        try {
            log("testMsgLogSegmented", "JCL submit: RC: " + po.getReturnCode());
            log("testMsgLogSegmented", "JCL submit: stdout:\n" + po.getStdout());
            log("testMsgLogSegmented", "JCL submit: stderr:\n" + po.getStderr());

            assertEquals(0, po.getReturnCode());

            String newJobId = zosOperationsFat.waitForNewJobId(SERVER_JOBNAME_CUSTOM1, prevJobId, 30);

            zosOperationsFat.waitForStringInJobLog(newJobId,
                                                   "CWWKF0011I: The server defaultServer is ready to run a smarter planet",
                                                   30);

            stopProcess(SERVER_JOBNAME_CUSTOM1, 30, false);

            List<String> joblog = ZosOperationsFat.getJoblogRaw(newJobId);

            assertNotNull("Joblog is null; job must have failed to start", joblog);

            log("testMsgLogSegmented", "joblog: ");
            for (String s : joblog) {
                log("testMsgLogSegmented", s);
            }

            // These are routed to hardcopy by LoggingHardcopyLogHandler
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0001I: Application userRegistry started", joblog));
            assertTrue(zosOperationsFat.isStringInLog(newJobId + "  +CWWKZ0009I: The application userRegistry has stopped successfully.", joblog));

            // Issued to STDOUT
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0001I: The server defaultServer has been launched", joblog));
            assertTrue(zosOperationsFat.isStringInLog("[AUDIT   ] CWWKE0036I: The server defaultServer stopped after", joblog));

            // MsgLogLogHandler messages
            assertTrue(zosOperationsFat.isStringInLog(" TRAS0018I: The trace state has been changed.", joblog));
            assertTrue(zosOperationsFat.isStringInLog(" CWWKB0104I: Authorized service group ", joblog));
        } finally {
            stopProcess(SERVER_JOBNAME_CUSTOM1, 5, true);
        }
    }

    @Test
    public void testMsgRouterConfig() throws Exception {
        String method = "testMsgRouterConfig";
        String wtoMsg = "TRAS0042I: The message routing group WTO contains the following messages: CWWKG9999E CWWKE4445I";
        String hardcopyMsg = "TRAS0042I: The message routing group HARDCOPY contains the following messages: CWWKG3333E CWWKG4444E CWWKG6666E CWWKG7777E";
        String wtoMsg2 = "TRAS0042I: The message routing group WTO contains the following messages: CWWKE4445I";
        String hardcopyMsg2 = "TRAS0042I: The message routing group HARDCOPY contains the following messages:";

        log(method, "Starting server with message router config");
        server.setServerConfigurationFile("msgRouterConfig.xml");
        server.startServerAndValidate(true, true, true);
        try {
            validateStringInLog(wtoMsg, method);
            validateStringInLog(hardcopyMsg, method);

            log(method, "Changing server configuration to secondary message router config");
            server.setServerConfigurationFile("msgRouterConfig2.xml");
            validateStringInLog(wtoMsg2, method);
            validateStringInLog(hardcopyMsg2, method);

            log(method, "Changing server configuration back to first message router config");
            server.setServerConfigurationFile("msgRouterConfig.xml");
            validateStringInLog(wtoMsg, method);
            validateStringInLog(hardcopyMsg, method);

            dumpLogFile(method);
        } finally {
            server.stopServer();
        }
    }

    private void validateStringInLog(String searchString, String method) throws Exception {
        String match = server.waitForStringInLogUsingLastOffset(searchString);
        if (match != null) {
            log(method, match);
        } else {
            // Did not find an expected string: dump the log and fail the test
            dumpLogFile(method);
            server.stopServer();
            fail(method + ": Expected string was not found in server log: " + searchString);
        }
    }

    private void dumpLogFile(String method) throws Exception {
        RemoteFile log = server.getDefaultLogFile();
        BufferedReader reader = new BufferedReader(new InputStreamReader(log.openForReading()));
        String line;
        while ((line = reader.readLine()) != null) {
            log(method, line);
        }
    }

    /**
     * Stops the process/job identified by the input jobname. If the process/job cannot be verified to be stopped, a cancel command is issued.
     * 
     * @param jobName The job name that identifies the process/job to be stopped.
     * @param timeout The time, in seconds, to wait for the job to become inactive.
     * @param force If true, the process is cancelled after an unsuccessful stop. If False, we just print a message.
     * @throws Exception
     */
    private static void stopProcess(String jobName, int timeout, boolean force) throws Exception {
        int iterations = (timeout <= 0) ? 1 : timeout;

        zosOperationsFat.stopWASProcess(jobName);
        String activeJobId = null;
        for (int i = 0; i < iterations; i++) {
            activeJobId = zosOperationsFat.getActiveJobId(jobName);

            // If there is no active jobid with the specified jobname, we are done.
            if (activeJobId == null) {
                return;
            }

            Thread.sleep(1000);
        }

        // We waited for "timeout" seconds. Cancel the process or just print a message.
        if (force) {
            zosOperationsFat.cancelWASProcess(jobName);
        } else {
            Log.warning(ZosLoggingTest.class, "stopProcess: Server with jobID: " + activeJobId + " did not stop. Time waited: " + timeout + " seconds.");
        }
    }
}