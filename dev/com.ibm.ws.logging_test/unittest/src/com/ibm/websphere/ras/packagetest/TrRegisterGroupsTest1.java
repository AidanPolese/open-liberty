package com.ibm.websphere.ras.packagetest;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.LoggingTestUtils;
import test.TestConstants;
import test.common.SharedOutputManager;

import com.ibm.websphere.ras.SharedTr;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.TraceOptions;

/**
 * Test TraceComponent registration methods using annotations to specify group
 * only
 */
public class TrRegisterGroupsTest1 {
    static {
        LoggingTestUtils.ensureLogManager();
    }
    static final String myName = TrRegisterGroupsTest1.class.getName();

    static SharedOutputManager outputMgr;

    static final Object[] objs = new Object[] { "p1", "p2", "p3", "p4" };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // make stdout/stderr "quiet"-- no output will show up for test
        // unless one of the copy methods or documentThrowable is called
        outputMgr = SharedOutputManager.getInstance();
        outputMgr.logTo(TestConstants.BUILD_TMP);
        outputMgr.captureStreams();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        SharedTr.clearComponents();

        // Make stdout and stderr "normal"
        outputMgr.restoreStreams();
    }

    @Before
    public void setUp() {
        TraceOptions options = this.getClass().getAnnotation(TraceOptions.class);
        System.out.println("options are: " + options);
        SharedTr.clearComponents();
    }

    @After
    public void tearDown() throws Exception {
        outputMgr.resetStreams();
    }

    @Test
    public void testRegisterClass() {
        final String m = "testRegisterClass";

        try {
            Class<?> myClass = this.getClass();

            TraceComponent tc = Tr.register(myClass);
            String str[] = tc.introspectSelf(); // returns name, group, and
            // bundle

            assertEquals(tc.getTraceClass(), myClass);
            assertEquals(str[0], "name = " + myName);
            assertEquals(str[1], "groups = [TestGroupName]");
            assertEquals(str[2], "bundle = " + "");
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }
}