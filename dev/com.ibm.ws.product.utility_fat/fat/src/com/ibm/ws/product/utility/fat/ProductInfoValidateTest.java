/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.product.utility.fat;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.log.Log;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 *
 */
public class ProductInfoValidateTest {

    public static Class<?> c = ProductInfoValidateTest.class;
    //don't really need a server for this test, but need to get the install paths
    public static LibertyServer server = LibertyServerFactory.getLibertyServer("com.ibm.ws.product.utility.test.validate.server");

    private static String installRoot;

    @BeforeClass
    public static void setup() throws Exception {
        final String METHOD_NAME = "setup";
        installRoot = server.getInstallRoot();
        Log.entering(c, METHOD_NAME);
        Log.info(c, METHOD_NAME, "installRoot: " + installRoot);
    }

    /**
     * Tests that we can run the productInfo validate command without exception.
     * Does not verify the output of the command, only that there were no exceptions
     * during the run of the command.
     * 
     * @throws Exception
     */
    @Test
    public void testProductInfoValidate() throws Exception {
        final String METHOD_NAME = "testProductInfoValidate";
        Log.entering(c, METHOD_NAME);
        String cmd = installRoot + "/bin/productInfo";
        String[] parms = new String[] { "validate" };

        ProgramOutput po = server.getMachine().execute(cmd, parms, installRoot);
        Log.info(c, METHOD_NAME, "productInfo validate stdout: ");
        Log.info(c, METHOD_NAME, po.getStdout());
        Log.info(c, METHOD_NAME, "productInfo validate stderr: ");
        Log.info(c, METHOD_NAME, po.getStderr());
        assertTrue("The productInfo validate command returned an error code, see autoFVT/results/output.txt log for detailed output", po.getReturnCode() == 0);

        Log.exiting(c, METHOD_NAME);
    }

}
