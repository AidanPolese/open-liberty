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
package componenttest.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.ibm.websphere.simplicity.log.Log;
import componenttest.topology.impl.LibertyServer;

/**
 * This inner class sets up the logging class name correctly for all subclassed FATs.
 */
public class CollectiveFailureDumpRule extends TestWatcher {

    private final LibertyServer server;
    private final String adminUser;
    private final String adminPassword;

    /**
     * @param server The server which is hosting the controller capabilities
     * @param mbsc The MBeanServerConnection for the given server
     */
    public CollectiveFailureDumpRule(final LibertyServer server, final String adminUser, final String adminPassword) {
        this.server = server;
        this.adminUser = adminUser;
        this.adminPassword = adminPassword;
    }

    @Override
    public void failed(final Throwable e, final Description desc) {
        if (server == null) {
            Log.info(desc.getTestClass(), desc.getMethodName(), "Test method failed. No collective controller available - skipping gather collective diagnostics");
        } else {
            Log.info(desc.getTestClass(), desc.getMethodName(), "Test method failed. Capturing Collective diagnostics");
            final String dumpFilePrefix = desc.getTestClass().getSimpleName() + "." + desc.getMethodName() + "_failureDump_" + System.currentTimeMillis();
            DumpUtils utils = new DumpUtils(server, adminUser, adminPassword);
            utils.dumpCollectiveRepository(desc, dumpFilePrefix);
            utils.captureCollectiveAPIDiagnostics(desc, dumpFilePrefix);
        }
    }

}