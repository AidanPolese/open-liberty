//%Z% %I% %W% %G% %U% [%H% %T%]
/**
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason       Version     Date        User id     Description
 * ----------------------------------------------------------------------------
 * 91946         8.5.5     13/06/2013    sumam     Test case for defect 91946.
 */
package com.ibm.ws.fat.hpel.tests;

import java.io.File;
import java.text.NumberFormat;
import java.util.logging.Logger;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.fat.VerboseTestCase;
import com.ibm.ws.fat.hpel.setup.HpelSetup;
import com.ibm.ws.fat.ras.util.CommonTasks;

/**
 * Test case for defect 91946 : HPEL configuration value doesn't revert to default when cleared from server.xml
 * While deleting the <logging> element purgeMaxSize property is not set to default value.
 * Test scenario
 * 1. Bootstrap = 91 , server.xml= 201 for Trace remove the entire <logging> element Trace = 91 and Log = 50
 * 2. server.xml= 201 for Trace remove the entire <logging> element Trace = 50 and Log = 50
 */

public class HpelLoggingElementDeleteTest extends VerboseTestCase {

    private final static String loggerName = HpelLoggingElementDeleteTest.class.getName();
    private final static Logger logger = Logger.getLogger(loggerName);
    private final static int MAX_DEFAULT_PURGE_SIZE = 100;

    private ApplicationServer appServ = null;

    RemoteFile backup = null;

    public HpelLoggingElementDeleteTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        // Call super.SetUp() cause we still want it's setup as well
        super.setUp();
        appServ = HpelSetup.getServerUnderTest();
        // Confirm HPEL is enabled
        if (!CommonTasks.isHpelEnabled(appServ)) {
            // HPEL is not enabled.
            this.logStep("HPEL is not enabled on " + appServ.getName() + ", attempting to enable.");
            CommonTasks.setHpelEnabled(appServ, true);
            // Restart now to complete switching to HPEL
            appServ.stop();
            appServ.start();
            this.logStepCompleted();
        }
        // Setting the bootstrap with trace specification to get the trace logs.

        CommonTasks.addBootstrapProperty(appServ, "com.ibm.ws.logging.trace.specification", "*=fine=enabled");
        appServ.stop();
        appServ.start();

        this.logStep("Configuring server for test case.");
        backup = new RemoteFile(appServ.getBackend().getMachine(), new File(appServ.getBackend().getServerRoot(), "server-backup.xml").getPath());
        if (!backup.exists()) {
            backup.copyFromSource(appServ.getBackend().getServerConfigurationFile());
        }
        appServ.getBackend().updateServerConfiguration(new File(appServ.getBackend().pathToAutoFVTTestFiles, "server-HpelLogElementDelete_1.xml"));
        if (!appServ.getBackend().isStarted()) {
            appServ.start();
        }

        this.logStepCompleted();

    }

    /**
     * Test 1. Set server.xml Trace = 201, during runtime remove the entire <logging> element,
     * run quick log and check for the repository size, both logdata and tracedata should not exceed more than 50 MB.
     **/

    public void testLoggingElementDelete_1() throws Exception {
        RemoteFile binaryLogDir = null;
        RemoteFile binaryTraceDir = null;

        NumberFormat nf = NumberFormat.getInstance();

        this.logStep("Configuring server for test case.");
        appServ.getBackend().updateServerConfiguration(new File(appServ.getBackend().pathToAutoFVTTestFiles, "server-HpelLogElementDelete_2.xml"));
        this.logStepCompleted();

        // write enough records to new log repository updated.
        this.logStep("Writting log records to fill binary log repository.");
        long loopsPerFullRepository = (MAX_DEFAULT_PURGE_SIZE * 1024 * 1024) / 200;
        logger.info("writting " + nf.format(loopsPerFullRepository) + " log loops to produce " + MAX_DEFAULT_PURGE_SIZE
                    + " MB of data.");
        CommonTasks.createLogEntries(appServ, loggerName, "Sample log record for the test case " + this.getName() + ".", null,
                                     (int) loopsPerFullRepository, CommonTasks.LOGS_TRACE, 0);

        this.logStepCompleted();

        this.logVerificationPoint("Verifying the repository size after deleting the logging element.");
        binaryLogDir = appServ.getBackend().getFileFromLibertyServerRoot("logs/logdata");
        long binaryLogSize = getSizeOfBinaryLogs(binaryLogDir);
        binaryTraceDir = appServ.getBackend().getFileFromLibertyServerRoot("logs/tracedata");
        long binaryTraceSize = getSizeOfBinaryLogs(binaryTraceDir);

        logger.info("The current size of BinaryLog files in " + binaryLogDir.getAbsolutePath() + " is " + nf.format(binaryLogSize));
        assertTrue("BinaryLog Repository size should be less than 50 MB ",
                   binaryLogSize > (45 * 1024 * 1024) && binaryLogSize < (50 * 1024 * 1024));
        logger.info("The current size of BinaryTrace files in " + binaryTraceDir.getAbsolutePath() + " is " + nf.format(binaryTraceSize));
        assertTrue("BinaryTrace Repository size should be less than 50 MB ",
                   binaryTraceSize > (45 * 1024 * 1024) && binaryTraceSize < (50 * 1024 * 1024));

        this.logVerificationPassed();

    }

    /**
     * Test 1. Bootstrap Trace = 91 ,in server.xml Trace = 201, during runtime remove the entire <logging> element
     * and run quick log, check the size of the repository it should not exceed more than 91 MB for trace data and 50 MB for log data.
     **/
    public void testLoggingElementDelete_2() throws Exception {
        RemoteFile binaryLogDir = null;
        RemoteFile binaryTraceDir = null;
        NumberFormat nf = NumberFormat.getInstance();

        CommonTasks.addBootstrapProperty(appServ, "com.ibm.hpel.trace.purgeMaxSize", "90");
        appServ.stop();
        appServ.start();

        this.logStep("Configuring server for test case.");
        appServ.getBackend().updateServerConfiguration(new File(appServ.getBackend().pathToAutoFVTTestFiles, "server-HpelLogElementDelete_1.xml"));
        this.logStepCompleted();

        appServ.stop();
        appServ.start();

        this.logStep("Configuring server for test case.");
        appServ.getBackend().updateServerConfiguration(new File(appServ.getBackend().pathToAutoFVTTestFiles, "server-HpelLogElementDelete_2.xml"));
        this.logStepCompleted();

        // write enough records to new log repository updated.
        this.logStep("Writting log records to fill binary log repository.");
        long loopsPerFullRepository = (MAX_DEFAULT_PURGE_SIZE * 1024 * 1024) / 200;
        logger.info("writting " + nf.format(loopsPerFullRepository) + " log loops to produce " + MAX_DEFAULT_PURGE_SIZE
                    + " MB of data.");
        CommonTasks.createLogEntries(appServ, loggerName, "Sample log record for the test case " + this.getName() + ".", null,
                                     (int) loopsPerFullRepository, CommonTasks.LOGS_TRACE, 0);

        this.logStepCompleted();

        this.logVerificationPoint("Verifying the repository size after deleting the logging element .");
        binaryLogDir = appServ.getBackend().getFileFromLibertyServerRoot("logs/logdata");
        long binaryLogSize = getSizeOfBinaryLogs(binaryLogDir);
        binaryTraceDir = appServ.getBackend().getFileFromLibertyServerRoot("logs/tracedata");
        long binaryTraceSize = getSizeOfBinaryLogs(binaryTraceDir);

        logger.info("The current size of BinaryLog files in " + binaryLogDir.getAbsolutePath() + " is " + nf.format(binaryLogSize));
        assertTrue("BinaryLog Repository size should be less than 50 MB ",
                   binaryLogSize > (45 * 1024 * 1024) && binaryLogSize < (50 * 1024 * 1024));
        logger.info("The current size of BinaryTrace files in " + binaryTraceDir.getAbsolutePath() + " is " + nf.format(binaryTraceSize));
        assertTrue("BinaryTrace Repository size should be less than 91 MB ",
                   binaryTraceSize > (85 * 1024 * 1024) && binaryTraceSize < (91 * 1024 * 1024));

        this.logVerificationPassed();

    }

    /**
     * Returns the total size of log files in the given directory
     * 
     * @throws Exception
     **/
    private long getSizeOfBinaryLogs(RemoteFile dirToCheck) throws Exception {

        long totalBinaryLogRepositorySize = 0;
        RemoteFile[] allBinaryLogFiles = dirToCheck.list(true);
        for (RemoteFile i : allBinaryLogFiles) {
            totalBinaryLogRepositorySize += i.length();
//            }
        }
        return totalBinaryLogRepositorySize;
    }

    @Override
    public void tearDown() throws Exception {
        // Restore values we saw before changing them in setUp()
        this.logStep("Resetting configuration to pre test values.");
        if (backup != null && backup.exists()) {
            appServ.getBackend().getServerConfigurationFile().copyFromSource(backup);
        }
        this.logStepCompleted();

        // call the super
        super.tearDown();
    }

}