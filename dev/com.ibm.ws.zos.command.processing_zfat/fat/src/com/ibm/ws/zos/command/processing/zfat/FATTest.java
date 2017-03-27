package com.ibm.ws.zos.command.processing.zfat;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.common.zos.ZosOperationsFat;

import com.ibm.websphere.simplicity.log.Log;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * Verify that command processing works by default
 * and that it does not with the bootstrap.properties update
 */
public class FATTest {
    private static LibertyServer server = LibertyServerFactory.getLibertyServer("COM.IBM.WS.ZOS.COMMAND.PROCESSING");
    private static String serverName;
    private static String installDir;
	private static File pidFile;
	private static String serversDir;
    
    /**
     * For use with logging
     */
    private static final Class<?> c = FATTest.class;
	private static final String WASJobName = "BBGZSRV";
	private int waitTime = 10;
	private String startMsgID = "CWWKF0011I";

	/**
	 * Start a server from the MVS console
	 * @throws Exception 
	 */
	private void startServerViaMVS() throws Exception {
    	String mName = "startServerViaMVS";
        String cmd = "S " + WASJobName + ",PARMS=" + serverName;
        Log.info(c, mName, "MVS Console Command: " + cmd);
        new ZosOperationsFat().executeMVSConsoleCommand(cmd);
	}
	
   /**
    * Start a server 
    * Stop it via the MVS Console
    * Verify it stopped
    */
    @Test
    public void testStopViaMVS() throws Exception {
    	String mName = "testStopViaMVS";
    	
        Log.info(c, mName, "Verify " + serverName + " is not running.");
    	assertFalse(pidFile.exists());
    	
        Log.info(c, mName, "Starting a new server to stop");
        startServerViaMVS();
        
        Log.info(c, mName, "Verify " + serverName + " started");
        ZosOperationsFat zosOp = new ZosOperationsFat();
        String result = zosOp.waitForStringInJobLog(WASJobName, waitTime, startMsgID);
    	assertTrue(pidFile.exists());
        assertTrue(result != null);

        Log.info(c, mName, "Verify that we can stop server " + serverName);
        zosOp.stopWASProcess(WASJobName);
    	assertFalse(pidFile.exists());
    }

   /** 
    * Update bootstrap.properties
    * Start the server
    * Attempt to stop it via the MVS Console
    * Verify that it did not stop
    */
    @Test
    public void testNoStopViaMVS() throws Exception {
    	String mName = "testNoStopViaMVS";
        Log.info(c, mName, "Starting a new server not to stop");

        server.copyFileToLibertyServerRoot("/NoConsole/bootstrap.properties");
        
        Log.info(c, mName, "Verify " + serverName + " is not running.");
    	assertFalse(pidFile.exists());
    	
        Log.info(c, mName, "Starting a new server NOT to stop");
        startServerViaMVS();

        
        Log.info(c, mName, "Verify " + serverName + " started");
        ZosOperationsFat zosOp = new ZosOperationsFat();
        String result = zosOp.waitForStringInJobLog(WASJobName, waitTime, startMsgID);
    	assertTrue(pidFile.exists());
        assertTrue(result != null);

        Log.info(c, mName, "Verify that we CANNOT stop server " + serverName);
        zosOp.stopWASProcess(WASJobName);
    	assertTrue(pidFile.exists());

        // should still be able to cancel the job without talking to the server
    	zosOp.executeMVSConsoleCommand("C " + WASJobName);

    	server.deleteFileFromLibertyServerRoot("bootstrap.properties");
        server.copyFileToLibertyServerRoot("/bootstrap.properties");
    }

    /**
     * Prepare for test
     * @throws Exception
     */
    @Before
    public void before() throws Exception { 
    	String mName = "before";
    	Log.info(c, mName, "Preparing for test...");
    	
        // precondition: no server or angel should be started. Stop if if there exist
        // either
        Log.info(c, mName, "Shutting down any existing angels and servers first");
        ZosOperationsFat zosOp = new ZosOperationsFat();
        zosOp.stopWASProcess(WASJobName);
        zosOp.stopAngel(true);

        installDir = server.getInstallRoot();
        serverName = server.getServerName();
        serversDir = installDir + File.separatorChar + "usr" + File.separatorChar + "servers";
        String pidFileName = serversDir + File.separatorChar + ".pid" + File.separatorChar + serverName + ".pid";
        pidFile = new File(pidFileName);
    	Log.info(c, mName, "Expected PID file: " + pidFile.getAbsolutePath());
    }
    
    /**
     * Cancel the server, just in case...
     * 
     * @throws Exception
     */
    @After
    public void after() throws Exception {   
    	String mName = "after";
    	Log.info(c, mName, "Cleaning up...");
    	new ZosOperationsFat().executeMVSConsoleCommand("C  " + WASJobName);
    }
}