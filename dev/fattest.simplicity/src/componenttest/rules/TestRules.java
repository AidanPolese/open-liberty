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

import org.junit.rules.TestRule;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

/**
 * Some helpful JUnit rules.
 */
public class TestRules {

    public interface PathGetter {
        TestRule onPath(String path);

        ServletGetter usingApp(String appName);
    }

    public interface ServletGetter {
        TestRule andServlet(String servletName);
    }

    /**
     * Run all tests with their method names using {@link FATServletClient}.
     * <p>
     * Examples: <br>
     * <code>@Rule public TestRule runAll = TestRules.runAllUsingTestNames(server).onPath("appName/servletName");</code> <br>
     * <code>@Rule public TestRule runAll = TestRules.runAllUsingTestNames(server).usingApp("appName").andServlet("servletName");</code>
     */
    public static PathGetter runAllUsingTestNames(final LibertyServer server) {
        return new PathGetter() {
            @Override
            public TestRule onPath(final String path) {
                return new RunFatClientUsingTestNamesRule(server, path);
            }

            @Override
            public ServletGetter usingApp(final String appName) {
                return new ServletGetter() {

                    @Override
                    public TestRule andServlet(final String servletName) {
                        final String path = appName + "/" + servletName;
                        return new RunFatClientUsingTestNamesRule(server, path);
                    }
                };
            }
        };
    }

    // prevent instantiation of static utility class
    private TestRules() {}
}
