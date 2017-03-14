/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.processing.internal.OSGiCommands;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 *
 */
public class OSGiConsoleTest {

    final Mockery context = new JUnit4Mockery();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * Test
     */
    @Test
    public void test_xxx() throws Exception {
        final BundleContext bundleContext = context.mock(BundleContext.class, "bundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });
        final Bundle bundle = context.mock(Bundle.class, "bundle");
        context.checking(new Expectations() {
            {
                allowing(bundle).getBundleContext();
                will(returnValue(bundleContext));
            }
        });

        String results = "results from test";

        String cmd = "scr list 51";
        byte[] readBuf = new byte[cmd.length()];
        OSGiConsole ch = new OSGiConsole(cmd);

        OutputStream out = ch.getOutput();
        out.write(results.getBytes());

        InputStream in = ch.getInput();
        assertEquals(cmd.length(), in.available());

        in.read(readBuf, 0, readBuf.length);
        assertEquals(cmd, new String(readBuf));

        ch.close();
        List<String> responses = ch.getResults();
        System.out.println("responses:" + responses.get(0));
        assertEquals(results, responses.get(0));
    }
}
