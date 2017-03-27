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

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.zos.ZosOperationsFat;

import com.ibm.websphere.simplicity.log.Log;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

public class ZosCommandHandlerTest {

    private static test.common.zos.ZosOperationsFat zops = null;

    private static final Class<ZosCommandHandlerTest> c = ZosCommandHandlerTest.class;

    private static ZosCommandHandlerUtility util = null;
    
    private static String servrStcId;

    protected static LibertyServer server = LibertyServerFactory.getLibertyServer("com.ibm.ws.zos.command.feature");

    @BeforeClass
    public static void setUp() throws Exception {
        Log.info(c, "setup", "Entry");
        
        servrStcId = null;

        util = new ZosCommandHandlerUtility(server);

        zops = new ZosOperationsFat();

        zops.executeMVSConsoleCommand("stop bbgzsrv");
        
        Thread.sleep(5000);

        zops.executeMVSConsoleCommand("s \"bbgzsrv,parms='com.ibm.ws.zos.command.feature'\"");
        servrStcId = ZosOperationsFat.waitForSTC(zops, "BBGZSRV", 10); 
        util.waitForOutputInLog(servrStcId, "CWWKF0011I", 10);         


        Log.info(c, "setup", "Exit");
    }

    @Test
    public void testCommandHandler() throws Exception {
        Log.info(c, "testCommandHandler", "Entry");
        boolean success = false;
        // Test command
        if (servrStcId != null && servrStcId.length() > 0) {
            zops.executeMVSConsoleCommand("F \"bbgzsrv,'test.command'\"");
            String servrJoblog = util.waitForOutputInLog(servrStcId, "UserProductExtensionCommandHandler_handleModify_respondingToModifyCommand: test.command", 10);
            if (servrJoblog.indexOf("COMPLETED SUCCESSFULLY") >= 0)
                success = true;           
        } else {
            Log.info(c, "testCommandHandler", "unable to determine BBGZSRV job id");
        }
        assertTrue("Successful completion message not found", success);

    }

    @Test
    public void testCommandHandlerHelp() throws Exception {
    	Log.info(c, "testCommandHandlerHelp", "Entry");
        boolean success = false;
    	// test for help
        if (servrStcId != null && servrStcId.length() > 0) {       
        	zops.executeMVSConsoleCommand("F \"bbgzsrv,help\"");
        	String servrJoblog = util.waitForOutputInLog(servrStcId, "CWWKB0012I: Test Command Handler has no help.", 10);
        	if (servrJoblog.indexOf("CWWKB0012I: Test Command Handler has no help.") >= 0) {
                success = true;      
                Log.info(c, "testCommandHandler", "help failed");
        	}
        } else {
            Log.info(c, "testCommandHandlerHelp", "unable to determine BBGZSRV job id");
        }
        assertTrue("Help message not found", success);
       

    }
    @AfterClass
    public static void tearDown() {
        if (zops != null) {
            try {
                zops.executeMVSConsoleCommand("stop bbgzsrv");
            } catch (Exception e) {
                Log.info(c, "tearDown", "Error stopping server " + e.toString());
            }
        }

    }

}
