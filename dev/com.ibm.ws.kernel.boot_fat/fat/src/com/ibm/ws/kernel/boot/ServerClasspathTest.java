/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;

import componenttest.custom.junit.runner.OnlyRunInJava7Rule;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * Tests what can and cannot be loaded by the server's JVM classpath.
 */
public class ServerClasspathTest {

    private static final String SERVER_NAME = "com.ibm.ws.kernel.boot.classpath.fat";

    private static final LibertyServer server = LibertyServerFactory.getLibertyServer(SERVER_NAME);

    private static final String[] EXPECTED_PACKAGES = { "com.ibm.ws.kernel", "java.", "javax.", "sun.",
                                                       "org.osgi.framework", "com.ibm.crypto", "com.ibm.security",
                                                       "com.ibm.misc", "com.ibm.xml", "com.ibm.nio", "com.ibm.jvm",
                                                       "org.apache.xerces", "com.ibm.Compiler", "com.ibm.oti",
                                                       "org.omg.CORBA", "com.sun", "org.xml.sax", "com.ibm.jit",
                                                       "com.ibm.jsse2", "com.ibm.lang.management", "com.ibm.tools.attach",
                                                       "com.ibm.virtualization.management", "com.ibm.wsspi.kernel",
                                                       "jdk.xml.internal", // Windows, Sun  
                                                       "jdk.net" // Java 8, Sun 1.7
    };

    @ClassRule
    public static final TestRule java7Rule = new OnlyRunInJava7Rule();

    @BeforeClass
    public static void before() throws Exception {
        server.startServer();
    }

    @AfterClass
    public static void after() throws Exception {
        server.stopServer();
    }

    @Test
    public void testJvmAppClasspath() throws Exception {
        //TODO: check logs for any packages that are not in the expected packages list
        StringBuilder unexpectedPackages = new StringBuilder();
        List<String> pkgsOnCP = server.findStringsInLogs("AppLoader can load: .*", server.getConsoleLogFile());
        Iterator<String> iter = pkgsOnCP.iterator();
        boolean allowed;
        while (iter.hasNext()) {
            allowed = false;
            String pkg = iter.next().substring("AppLoader can load: ".length());
            for (String allowedPkg : EXPECTED_PACKAGES) {
                if (pkg.startsWith(allowedPkg)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                unexpectedPackages.append(" " + pkg);
            }
        }
        assertEquals("Found unexpected packages in the server JVM's application classpath", "", unexpectedPackages.toString());
    }
}
