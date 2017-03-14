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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Dictionary;
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
import org.osgi.framework.ServiceRegistration;

import com.ibm.ws.zos.command.processing.internal.ModifyResultsImpl;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 *
 */
public class OSGiCommandHandlerTest {

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
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.OSGiCommands.OSGiHandlerHolder#getHelp()}.
     */
    @Test
    public void test_activate_deactivate() throws Exception {
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

        OSGiCommandHandler ch = new OSGiCommandHandler();
        ch.activate(bundleContext);
        assertEquals(bundleContext, ch.bundleContext);

        ch.deactivate(bundleContext);
        assertEquals(null, ch.bundleContext);
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.OSGiCommands.OSGiHandlerHolder#getHelp()}.
     */
    @Test
    public void test_getHelp() throws Exception {
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

        OSGiCommandHandler ch = new OSGiCommandHandler();

        List<String> helpStrings = ch.getHelp();
        List<String> expectedResults = Arrays.asList("Issue \"MODIFY <jobname.>identifier,\'osgi,osgicmd\'\"",
                                                     "  where osgicmd is a valid OSGI console command");

        assertEquals(expectedResults, helpStrings);

    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.OSGiCommands.OSGiHandlerHolder#getHelp()}.
     */
    @Test
    public void test_getName() throws Exception {
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

        OSGiCommandHandler ch = new OSGiCommandHandler();
        String handlerName = ch.getName();

        assertEquals(OSGiCommandHandler.OSGICOMMANDHANDLERNAME, handlerName);
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.OSGiCommands.OSGiHandlerHolder#getHelp()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_handleModify() throws Exception {
        final BundleContext bundleContext = context.mock(BundleContext.class, "bundleContext");
        context.checking(new Expectations() {
            {
                allowing(bundleContext).registerService(with(any(String.class)), with(any(Object.class)), with(any(Dictionary.class)));
                will(returnValue(null));
                //ignoring(bundleContext);
            }
        });
        final Bundle bundle = context.mock(Bundle.class, "bundle");
        context.checking(new Expectations() {
            {
                allowing(bundle).getBundleContext();
                will(returnValue(bundleContext));

            }
        });

        final List<String> cmdResults = Arrays.asList("First line from OSGI",
                                                      "Second line from OSGI",
                                                      "Last line from OSGI");

        OSGiCommandHandler ch = new OSGiCommandHandler() {
            @Override
            protected OSGiConsole getNewConsole(String osgiCmd) throws UnsupportedEncodingException {
                OSGiConsole myOSGiConsole = new OSGiConsole(osgiCmd) {
                    @Override
                    protected List<String> getResults() {
                        return cmdResults;
                    }
                };
                return myOSGiConsole;
            }

            @Override
            protected ServiceRegistration<?> registerConsole(OSGiConsole oConsole) {
                return null;
            }
        };

        ModifyResultsImpl results = new ModifyResultsImpl();
        results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
        results.setResponsesContainMSGIDs(false);

        String commandString = "osgi,scr list 51";

        ch.handleModify(commandString, results);

        assertEquals(ModifyResults.PROCESSED_COMMAND, results.getCompletionStatus());
        assertEquals(false, results.responsesContainMSGIDs());
        assertEquals(cmdResults, results.getResponses());
    }
}
