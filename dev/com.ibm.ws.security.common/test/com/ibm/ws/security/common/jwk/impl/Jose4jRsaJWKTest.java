package com.ibm.ws.security.common.jwk.impl;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.ibm.ws.security.common.jwk.impl.Jose4jRsaJWK;

import test.common.SharedOutputManager;

public class Jose4jRsaJWKTest {

    private static SharedOutputManager outputMgr = SharedOutputManager.getInstance().trace("com.ibm.ws.security.common.*=all");

    private final String UTF_8 = "UTF-8";
    private final String RSA = "RSA";
    private final String RS256 = "RS256";
    private final String HS256 = "HS256";

    private final Mockery mockery = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    public interface MockInterface {
    }

    final MockInterface mockInterface = mockery.mock(MockInterface.class);

    @Rule
    public final TestName testName = new TestName();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        outputMgr.captureStreams();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        outputMgr.dumpStreams();
        outputMgr.restoreStreams();
    }

    @Before
    public void beforeTest() {
        System.out.println("Entering test: " + testName.getMethodName());
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Exiting test: " + testName.getMethodName());
        outputMgr.resetStreams();
        mockery.assertIsSatisfied();
    }

    /**
     * Method(s) under test:
     * <ul>
     * <li></li>
     * </ul>
     */
    @Test
    public void testGetInstance() {
        try {
            Jose4jRsaJWK result = Jose4jRsaJWK.getInstance(512, RS256, null, RSA);

        } catch (Throwable t) {
            outputMgr.failWithThrowable(testName.getMethodName(), t);
        }
    }

}
