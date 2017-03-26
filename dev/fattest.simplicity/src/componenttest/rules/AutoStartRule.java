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
 * Automatically start a given LibertyServer before tests.
 */
class AutoStartRule extends ExternalResource {
    private final LibertyServer server;

    public AutoStartRule(final LibertyServer server) {
        this.server = server;
    }

    @Override
    protected void before() throws Throwable {
        server.startServer();
    }
}