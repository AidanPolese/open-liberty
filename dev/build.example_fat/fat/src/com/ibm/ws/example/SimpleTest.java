package com.ibm.ws.example;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import app1.web.TestServletA;
import componenttest.annotation.MinimumJavaLevel;
import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

/**
 * Example Shrinkwrap FAT project:
 * <li> Uses minimal ant scritping, so that the bucket can be run using the green
 * 'play' button in an eclipse IDE or with the traditional build-test.xml via ant.
 * <li> Application packaging is done in the @BeforeClass, instead of ant scripting.
 * <li> Injects servers via @Server annotation. Annotation value corresponds to the
 * server directory name in 'publish/servers/%annotation_value%' where ports get
 * assigned to the LibertyServer instance when the 'testports.properties' does not
 * get used.
 * <li> Specifies an @RunWith(FATRunner.class) annotation. Traditionally this has been
 * added to bytecode automatically by ant, and is a small downside to factoring out ant.
 * <li> Uses the @TestServlet annotation to define test servlets. Notice that no @Test
 * methods are defined in this class. All of the @Test methods are defined on the test
 * servlet referenced by the annotation, and will be run whenever this test class runs.
 */
@RunWith(FATRunner.class)
@MinimumJavaLevel(javaLevel = 1.7)
public class SimpleTest extends FATServletClient {

    @Server("FATServer")
    @TestServlet(servlet = TestServletA.class, path = "app1/TestServletA")
    public static LibertyServer server1;

    @BeforeClass
    public static void setUp() throws Exception {
        // Create a WebArchive that will have the file name 'app1.war' once it's written to a file
        // Include the 'app1.web' package and all of it's java classes and sub-packages
        // Include a simple index.jsp static file in the root of the WebArchive
        WebArchive app1 = ShrinkWrap.create(WebArchive.class, "app1.war")//
                        .addPackages(true, "app1.web")//
                        .addAsWebInfResource(new File("test-applications/app1/resources/index.jsp"));
        // Write the WebArchive to 'publish/servers/FATServer/apps/app1.war' and print the contents
        ShrinkHelper.exportAppToServer(server1, app1);

        server1.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server1.stopServer();
    }
}
