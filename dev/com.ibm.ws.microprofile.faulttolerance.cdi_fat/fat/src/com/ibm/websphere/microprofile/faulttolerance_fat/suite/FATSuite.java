package com.ibm.websphere.microprofile.faulttolerance_fat.suite;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

import java.io.File;

 
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import com.ibm.websphere.simplicity.ShrinkHelper;

import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDIAsyncTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDICircuitBreakerTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDIFallbackTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDIRetryTest;
import com.ibm.websphere.microprofile.faulttolerance_fat.tests.CDITimeoutTest;




@RunWith(Suite.class)
@SuiteClasses({
                CDIAsyncTest.class,
                //temporarily disabled while api is updated
//                CDIBulkheadTest.class,
                CDICircuitBreakerTest.class,
                CDIFallbackTest.class,
                CDIRetryTest.class,
                CDITimeoutTest.class
})
public class FATSuite {
	
	@BeforeClass
	public static void setUp() throws Exception {
	String APP_NAME = "CDIFaultTolerance";
	
	JavaArchive faulttolerance_jar = ShrinkWrap.create(JavaArchive.class, "faulttolerance.jar").addPackages(true,"com.ibm.ws.microprofile.faulttolerance_fat.util");
	WebArchive CDIFaultTolerance_war = ShrinkWrap.create(WebArchive.class, APP_NAME + ".war")
					.addPackages(true, "com.ibm.ws.microprofile.faulttolerance_fat.cdi")
					.addAsLibraries(faulttolerance_jar)
					.addAsManifestResource(new File("test-applications/" + APP_NAME + ".war/resources/META-INF/permissions.xml"), "persistence.xml");
					
	ShrinkHelper.exportArtifact(CDIFaultTolerance_war, "publish/servers/CDIFaultTolerance/dropins/");				
}
	
	
}
