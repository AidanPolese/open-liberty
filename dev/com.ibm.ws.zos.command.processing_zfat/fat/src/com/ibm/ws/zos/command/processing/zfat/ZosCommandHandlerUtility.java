/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.command.processing.zfat;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.log.Log;
import componenttest.topology.impl.LibertyServer;

/**
 *
 */
public class ZosCommandHandlerUtility {

    private static final Class<ZosCommandHandlerUtility> c = ZosCommandHandlerUtility.class;

    private final LibertyServer server;

    private static final int JOB_SEARCH_TIMEOUT = 5; // seconds

    protected ZosCommandHandlerUtility(LibertyServer server) {
        this.server = server;
    }

    /**
     * Wait for some text to appear in a joblog / STC log, up to a timeout.
     * 
     * @return the entire joblog
     */
    protected String waitForOutputInLog(String jobId, String text, int timeoutInSeconds) throws Exception {
        // This loop is somewhat silly, unfortunately.  We want to loop until we have
        // job output.  However, sometimes the job output will seem available, but is
        // not complete (sections are missing).  If the caller has provided text to
        // search for, we will wait to see if that text appears in the log, and if not,
        // we'll keep waiting, up to our timeout.  The caller still needs to check if
        // that output appears in the log.
        int i = 0;
        String myJobLog = getRecentJoblog(jobId);
        String textClue = (text != null) ? text : " ";
        while (((myJobLog == null) || (myJobLog.isEmpty()) || (myJobLog.contains(textClue) == false))
               && (i++ < JOB_SEARCH_TIMEOUT)) { // give it a few seconds even though our methods are 100% foolproof
            Thread.sleep(1000);
            myJobLog = getRecentJoblog(jobId);
        }
        return myJobLog;
    }

    /**
     * Get joblog with jobId
     * should this return ZWASJoblogReader ?? instead of String
     * 
     * @param jobId JobID of the job
     * @throws Exception
     */
    private String getRecentJoblog(String jobId) throws Exception {
        Log.info(c, "getJoblog", "get job log of most recent finished job");
        String joblog = null;
        Machine machine = Machine.getLocalMachine();

        if (jobId != null) {
            ProgramOutput jobout = machine.execute("sysout",
                                                   new String[] { "-o", jobId },
                                                   "/usr/local/bin");
            joblog = jobout.getStdout();
        }
        return joblog;
    }

}
