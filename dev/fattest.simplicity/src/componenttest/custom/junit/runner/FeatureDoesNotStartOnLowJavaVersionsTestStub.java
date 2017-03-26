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
package componenttest.custom.junit.runner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.ibm.websphere.simplicity.log.Log;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * This is a special test which the test framework decides it should run if a test class would
 * otherwise not run any tests because the Java level being run isn't one where running the
 * tests makes sense.
 */
public class FeatureDoesNotStartOnLowJavaVersionsTestStub {

    private static Class<FeatureDoesNotStartOnLowJavaVersionsTestStub> c = FeatureDoesNotStartOnLowJavaVersionsTestStub.class;

    public static String SYNTHETIC_METHOD_NAME = "testFeatureDidNotStartWhenJavaIsTooLow";
    /**
     * Looks for messages of the form [12/09/14 21:42:35:513 BST] 0000002c id= com.ibm.ws.kernel.feature.internal.FeatureManager E CWWKF0032E: The
     * com.ibm.websphere.appserver.javax.interceptor-1.2 feature requires a minimum Java runtime environment version of JavaSE 1.7.
     */
    private static final String FEATURE_DID_NOT_MEET_PREREQS_MESSAGE_CODE = "CWWKF0032E";
    private final String testClassName;

    public FeatureDoesNotStartOnLowJavaVersionsTestStub(String testClassName)
    {
        this.testClassName = testClassName;
    }

    /**
     * This is a JUnit test intended to be fake-hosted by other classes.
     */
    @Test
    public void testFeatureDidNotStartWhenJavaIsTooLow() throws Exception
    {

        // Cheat to find the server we're probably supposed to be testing
        Collection<LibertyServer> servers = LibertyServerFactory.getKnownLibertyServers(testClassName);
        boolean isClient = isClient();
        if (servers.isEmpty() && !isClient)
        {
            fail("There were no servers for test class "
                 + testClassName
                 + ". Ensure that you have a class or method setUp method which starts a server (or use a @ClassRule or @Rule annotation on the field which declares the liberty server).");
        }
        else if (!servers.isEmpty() && isClient) {
            fail(testClassName
                 + " is marked ClientOnly but appears to create servers. Ensure that you're not setting up servers in a class or method setUp method, or using a @ClassRule or @Rule that sets up servers.");
        }

        // We'll get servers from previous tests here (LibertyServerFactory doesn't clean up knownServers), so be cautious
        for (LibertyServer server : servers) {
            // make sure there are logs indicating a server start - if not, don't check anything 
            // We can do a find, not a wait, because the LibertyServer will already have waited if there's anything to find
            List<String> started = null;
            try {
                started = server.findStringsInLogs("CWWKF0011I");
            } catch (FileNotFoundException e)
            {
                // If we don't have a log this is a defunct (cleaned-up) server, so don't check anything
            }
            if (started != null && !started.isEmpty())
            {

                String line = server.waitForStringInLog(FEATURE_DID_NOT_MEET_PREREQS_MESSAGE_CODE);
                assertNotNull("The server logs for server "
                              + server.getServerName()
                              + " did not report that a feature did not start (was looking for "
                              + FEATURE_DID_NOT_MEET_PREREQS_MESSAGE_CODE
                              + "). \n" + getWhereThisCameFromMessage() +
                              "Checked all server logs for " + servers.size() + " servers started by " + testClassName + ".)",
                              line);
                // It's unclear whether we should check the feature is included in the message or not - it could be a source of spurious failures if the server 
                // names a different feature than the one under test

                // The server logs are checked for errors, and hence this test will fail unless
                // The relevant messages are ignored.
                ArrayList<String> errors = new ArrayList<String>();
                errors.add("CWWKF0032E"); //E CWWKF0032E: The <feature-name> feature requires a minimum Java runtime environment version of "java-version".                
                errors.add("CWWKE0702E"); //E CWWKE0702E: Could not resolve module: <module-name>
                errors.add("CWWKZ0124E"); //E CWWKZ0124E: Application <application-name> does not contain any modules.
                errors.add("CWWKE0701E"); //E CWWKE0701E: FrameworkEvent ERROR Bundle:com.ibm.websphere.appserver.thirdparty.eclipselink(id=116) org.osgi.framework.BundleException: Error starting module.
                server.addIgnoredErrors(errors);

            }
            // Otherwise the server may be an old one which is cleared up

        }

    }

    protected String getWhereThisCameFromMessage() {
        return "(If you're wondering where this test came from, it is a synthetic test which was added because the running test declares that the feature being tested requires a minimum java level.\n";
    }

    private boolean isClient() {
        try {
            Class<?> clazz = Class.forName(testClassName);
            if (clazz.getAnnotation(ClientOnly.class) != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            Log.error(c, "isClient", e);
        }
        return false;
    }
}
