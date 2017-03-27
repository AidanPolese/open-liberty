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

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import componenttest.topology.impl.LibertyServer;

/**
 * JUnit rules for doing stuff with LibertyServers before and after tests e.g. starting and stopping.
 */
public final class ServerRules {

    /**
     * Use this rule to automatically start a given Liberty server before tests.
     * E.g. <code>@Rule public TestRule startRule = ServerRules.startAutomatically(myServer);</code>
     */
    public static TestRule startAutomatically(final LibertyServer server) {
        return new AutoStartRule(server);
    }

    /**
     * Use this rule to automatically stop a given Liberty server after tests.
     * E.g. <code>@Rule public TestRule stopRule = ServerRules.stopAutomatically(myServer);</code>
     */
    public static TestRule stopAutomatically(final LibertyServer server) {
        return new AutoStopRule(server);
    }

    /**
     * Use this rule to automatically start and stop a given Liberty server before and after tests.
     * E.g. <code>@Rule public TestRule startStopRule = ServerRules.startAndStopAutomatically(myServer);</code>
     */
    public static TestRule startAndStopAutomatically(final LibertyServer server) {
        return RuleChain.outerRule(new AutoStartRule(server)).around(new AutoStopRule(server));
    }

    // prevent instantiation of static utility class
    private ServerRules() {}

}
