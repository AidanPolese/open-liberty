package com.ibm.ws.threading.policy;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import web.PolicyExecutorServlet;

@RunWith(FATRunner.class)
public class PolicyExecutorTest extends FATServletClient {

    @Server("PolicyExecutorServer")
    @TestServlet(servlet = PolicyExecutorServlet.class, path = "basicfat/PolicyExecutorServlet")
    public static LibertyServer server1;

    @BeforeClass
    public static void setUp() throws Exception {
        server1.copyFileToLibertyInstallRoot("lib/features/", "features/policyExecutorUser-1.0.mf");
        server1.copyFileToLibertyInstallRoot("lib/", "bundles/test.policyexecutor.bundle_fat.jar");

        WebArchive app = ShrinkWrap.create(WebArchive.class, "basicfat.war")//
                        .addPackages(true, "web")//
                        .addAsWebInfResource(new File("test-applications/basicfat/resources/index.jsp"));
        ShrinkHelper.exportAppToServer(server1, app);

        server1.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server1.stopServer();
        server1.deleteFileFromLibertyInstallRoot("lib/features/policyExecutorUser-1.0.mf");
        server1.deleteFileFromLibertyInstallRoot("lib/test.policyexecutor.bundle_fat.jar");
    }
}
