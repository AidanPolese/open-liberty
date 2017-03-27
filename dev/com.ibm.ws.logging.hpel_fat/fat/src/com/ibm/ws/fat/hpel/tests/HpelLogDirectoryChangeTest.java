//%Z% %I% %W% %G% %U% [%H% %T%]
/**
 * 
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
 * 98216        8.5.5     13/06/2013    sumam    Test case for defect 98216.
 * 
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
 * Test case for defect 98216 : HPEL doesn't change location of logdata/tracedata when logDirectory changes.
 * While changing the logDirectory attribute of <logging> element, the new logs were going inside the old directory.
 * Test Scenario -> Start the server with logDirectory as logs → during runtime change the logDirectory to logX and run quick log, new logs should be generated under the logx
 * repository.
 * 
 */

public class HpelLogDirectoryChangeTest extends VerboseTestCase {

    private final static String loggerName = HpelLogDirectoryChangeTest.class.getName();
    private final static Logger logger = Logger.getLogger(loggerName);
    private final static int MAX_DEFAULT_PURGE_SIZE = 30;

    private ApplicationServer appServ = null;

    RemoteFile backup = null;

    public HpelLogDirectoryChangeTest(String name) {
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
        this.logStep("Configuring server for test case.");
        backup = new RemoteFile(appServ.getBackend().getMachine(), new File(appServ.getBackend().getServerRoot(), "server-backup.xml").getPath());
        if (!backup.exists()) {
            backup.copyFromSource(appServ.getBackend().getServerConfigurationFile());
        }
        // Setting the log directory of logs
        appServ.getBackend().updateServerConfiguration(new File(appServ.getBackend().pathToAutoFVTTestFiles, "server-HpelLogDirectoryChange_1.xml"));
        if (!appServ.getBackend().isStarted()) {
            appServ.start();
        }

        this.logStepCompleted();

    }

    /**
     * Testing the logDirectory attribute by changing to new location and running the quick logs.
     * Start the server with logDirectory as logs, during runtime change the value of logDirectory to logx, run quick logs
     * and check the location where logs are getting stored.
     **/
    public void testLogDirectoryChange() throws Exception {
        RemoteFile binaryLogDir = null;
        NumberFormat nf = NumberFormat.getInstance();

        this.logStep("Configuring server for test case by setting the logDirectory to logx");
        appServ.getBackend().updateServerConfiguration(new File(appServ.getBackend().pathToAutoFVTTestFiles, "server-HpelLogDirectoryChange_2.xml"));
        this.logStepCompleted();

        // write enough records to new log repository updated.
        this.logStep("Writting log records to fill binary log repository.");
        long loopsPerFullRepository = (MAX_DEFAULT_PURGE_SIZE * 1024 * 1024) / 200;
        logger.info("writting " + nf.format(loopsPerFullRepository) + " log loops to produce " + MAX_DEFAULT_PURGE_SIZE
                    + " MB of data.");
        CommonTasks.createLogEntries(appServ, loggerName, "Sample log record for the test case " + this.getName() + ".", null,
                                     (int) loopsPerFullRepository, CommonTasks.LOGS, 0);

        this.logStepCompleted();

        this.logVerificationPoint("Verifying the repository used for log is new location (logx).");
        binaryLogDir = appServ.getBackend().getFileFromLibertyServerRoot("logx/logdata");
        long binaryLogSize = getSizeOfBinaryLogs(binaryLogDir);
        logger.info("The current size of BinaryLog files in " + binaryLogDir.getAbsolutePath() + " is " + nf.format(binaryLogSize));
        assertTrue("BinaryLog Repository should be the new location logx ",
                   binaryLogSize > ((MAX_DEFAULT_PURGE_SIZE - 5) * 1024 * 1024) && binaryLogSize < (50 * 1024 * 1024));
        this.logVerificationPassed();

    }

    /**
     * Returns the total size of log files in the given directory
     * 
     * @throws Exception
     **/
    private long getSizeOfBinaryLogs(RemoteFile dirToCheck) throws Exception {
        long totalgRepositorySize = 0;
        RemoteFile[] allLogFiles = dirToCheck.list(true);
        for (RemoteFile i : allLogFiles) {
            totalgRepositorySize += i.length();
        }
        return totalgRepositorySize;
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