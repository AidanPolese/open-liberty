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

import org.junit.rules.ExternalResource;

import componenttest.topology.impl.LibertyServer;

/**
 * Automatically stop the given LibertyServer after tests.
 */
class AutoStopRule extends ExternalResource {
    private final LibertyServer server;

    public AutoStopRule(final LibertyServer server) {
        this.server = server;
    }

    @Override
    protected void after() {
        if (server != null && server.isStarted()) {
            try {
                server.stopServer();
            } catch (final Exception e) {
                // not much we can do?
                e.printStackTrace();
            }
        }
    }
}