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
package com.ibm.ws.zos.command.processing.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 *
 */
public class CommandHandlerHolderTest {

    final Mockery context = new JUnit4Mockery();
    CommandProcessor commandProcessor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        commandProcessor = new CommandProcessor();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        commandProcessor = null;
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#compareTo()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCompareTo() throws Exception {
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("trace"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(20L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(200));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("true"));
            }
        });

        final ServiceReference<CommandHandler> handlerReference1 = context.mock(ServiceReference.class, "handlerReference1");
        context.checking(new Expectations() {
            {
                allowing(handlerReference1).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("trace"));
                allowing(handlerReference1).getProperty(Constants.SERVICE_ID);
                will(returnValue(20L));
                allowing(handlerReference1).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(200));
                allowing(handlerReference1).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("true"));
            }
        });

        final ServiceReference<CommandHandler> handlerReference2 = context.mock(ServiceReference.class, "handlerReference2");
        context.checking(new Expectations() {
            {
                allowing(handlerReference2).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("trace"));
                allowing(handlerReference2).getProperty(Constants.SERVICE_ID);
                will(returnValue(25L));
                allowing(handlerReference2).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(200));
                allowing(handlerReference2).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("false"));
            }
        });

        final ServiceReference<CommandHandler> handlerReference3 = context.mock(ServiceReference.class, "handlerReference3");
        context.checking(new Expectations() {
            {
                allowing(handlerReference3).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("trace"));
                allowing(handlerReference3).getProperty(Constants.SERVICE_ID);
                will(returnValue(30L));
                allowing(handlerReference3).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(50));
                allowing(handlerReference3).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("FALSE"));
            }
        });

        CommandHandlerHolder holder = new CommandHandlerHolder(commandProcessor, handlerReference);
        CommandHandlerHolder holder1 = new CommandHandlerHolder(commandProcessor, handlerReference1);
        CommandHandlerHolder holder2 = new CommandHandlerHolder(commandProcessor, handlerReference2);
        CommandHandlerHolder holder3 = new CommandHandlerHolder(commandProcessor, handlerReference3);

        assertEquals(0, holder.compareTo(holder));
        assertEquals(false, holder.equals(null));
        assertEquals(0, holder.compareTo(holder1));
        assertEquals(-1, holder1.compareTo(holder2));
        assertEquals(1, holder2.compareTo(holder1));
        assertEquals(-1, holder2.compareTo(holder3));
        assertEquals(1, holder3.compareTo(holder2));

    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#getServiceReference()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHandlerHolder() throws Exception {
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("trace"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(20L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(200));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("true"));
            }
        });

        CommandHandlerHolder holder = new CommandHandlerHolder(commandProcessor, handlerReference);
        assertSame(commandProcessor, holder.commandProcessor);
        assertSame(handlerReference, holder.getServiceReference());
        assertEquals("trace", holder.filterSpec);
        assertEquals(200, holder.serviceRanking);
        assert (holder.displayHelp());
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#getServiceReference()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testGetServiceReference() {
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("display,work"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(40L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(2000));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("false"));
            }
        });

        CommandHandlerHolder holder = new CommandHandlerHolder(commandProcessor, handlerReference);
        assertSame(commandProcessor, holder.commandProcessor);
        assertSame(handlerReference, holder.getServiceReference());
        assertEquals("display,work", holder.filterSpec);
        assertEquals(40L, holder.serviceId);
        assertEquals(2000, holder.serviceRanking);
        assert (holder.displayHelp() == false);
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#getService()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testGetService() {
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("display,getservice"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(22L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(null));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        final String Handler1Name = "TestHandler1";
        final CommandHandler mockHandler = context.mock(CommandHandler.class, "wsCommandHandler");
        context.checking(new Expectations() {
            {
                allowing(mockHandler).getName();
                will(returnValue(Handler1Name));
                //    allowing(mockHandler).handleModify(modifyCommmand1);
                //    will(returnValue(modifyCommmand1Response));
            }
        });

        final BundleContext bundleContext = context.mock(BundleContext.class, "CommandProcessorBundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });
        final ComponentContext componentContext = context.mock(ComponentContext.class, "CommandProcessorComponentContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                oneOf(componentContext).locateService(commandProcessor.getWsHandlerReferenceName(), handlerReference);
                will(returnValue(mockHandler));
            }
        });

        // Need to override the activate, skip thread stuff.
        final CommandProcessor testCommandProcessor = new CommandProcessor() {

            @Override
            protected void activate(ComponentContext cc) {

            }

            @Override
            ComponentContext getComponentContext() {
                return componentContext;
            }

            @Override
            protected String getWsHandlerReferenceName() {
                return WS_COMMAND_HANDLER_REFERENCE_NAME;
            }
        };

        // Activate the Command processor
        testCommandProcessor.activate(componentContext);

        // Create a handler holder for the test service
        CommandHandlerHolder holder = new CommandHandlerHolder(testCommandProcessor, handlerReference);
        CommandHandler handler = holder.getService();
        assertNotNull(handler);
        assertSame(mockHandler, handler);

        // Should be cached now, make sure we get the same instance back.
        assertSame(handler, holder.getService());
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#getHelp()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testGetHelp() {

        final String filterString = "((?i)(trace).*)";
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue(filterString));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(23L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(2023));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("true"));
            }
        });

        final List<String> modifyCommmand1Responses = new ArrayList<String>();
        modifyCommmand1Responses.add("Native Logging received command");
        modifyCommmand1Responses.add("Second/Last response");

        final ModifyResults mockResults = context.mock(ModifyResults.class, "mockResults");
        context.checking(new Expectations() {
            {
                allowing(mockResults).getResponses();
                will(returnValue(modifyCommmand1Responses));
                allowing(mockResults).getCompletionStatus();
                will(returnValue(ModifyResults.PROCESSED_COMMAND));
            }
        });

        final String Handler1Name = "TestHandler1";
        final String modifyCommmand1 = "TRACE,something";
        final CommandHandler mockHandler = context.mock(CommandHandler.class, "wsCommandHandler");
        context.checking(new Expectations() {
            {
                allowing(mockHandler).getName();
                will(returnValue(Handler1Name));
                allowing(mockHandler).handleModify(modifyCommmand1, mockResults);
            }
        });
        final BundleContext bundleContext = context.mock(BundleContext.class, "CommandProcessorBundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });

        final CommandHandlerTest commandHandlerTest = new CommandHandlerTest();

        final ComponentContext componentContext = context.mock(ComponentContext.class, "CommandProcessorComponentContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                oneOf(componentContext).locateService(commandProcessor.getWsHandlerReferenceName(), handlerReference);
                will(returnValue(commandHandlerTest));
            }
        });

        final CommandProcessor testCommandProcessor = new CommandProcessor() {

            @Override
            protected void activate(ComponentContext cc) {

            }

            @Override
            ComponentContext getComponentContext() {
                return componentContext;
            }

            @Override
            protected String getWsHandlerReferenceName() {
                return WS_COMMAND_HANDLER_REFERENCE_NAME;
            }
        };

        // Activate the Command processor
        testCommandProcessor.activate(componentContext);

        // Create a handler holder for the test service
        CommandHandlerHolder holder = new CommandHandlerHolder(testCommandProcessor, handlerReference);

        List<String> results = holder.getHelp();
        List<String> expectedResults = Arrays.asList("Help text for CommandHandlerTest, line 1",
                                                     "Help text for CommandHandlerTest, line 2",
                                                     "Help text for CommandHandlerTest, line 3",
                                                     "Help text for CommandHandlerTest, line 4");
        assertEquals(expectedResults, results);
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#getFilter()}.
     */
    @Ignore
    @SuppressWarnings("unchecked")
    @Test
    public final void testGetFilter() throws Exception {
        final String filterString = "(LOGGING.*)";

        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue(filterString));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(23L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(2023));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });
        final Pattern mockFilter = context.mock(Pattern.class, "filter");

        final BundleContext bundleContext = context.mock(BundleContext.class, "CommandProcessorBundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });

        final ComponentContext componentContext = context.mock(ComponentContext.class, "CommandProcessorComponentContext");
        context.checking(new Expectations() {
            {
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                ignoring(componentContext);
            }
        });

        // Activate the Command processor
        commandProcessor.activate(componentContext);

        // Create a handler holder for the test service
        CommandHandlerHolder holder = new CommandHandlerHolder(commandProcessor, handlerReference);
        Pattern filter = holder.getFilter();
        assertSame(filter, mockFilter);
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#deliverCommand(java.lang.String)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testDeliverCommand() {
        final String filterString = "((?i)(trace).*)";
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue(filterString));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(23L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(2023));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        final List<String> modifyCommmand1Responses = new ArrayList<String>();
        modifyCommmand1Responses.add("Native Logging received command");
        modifyCommmand1Responses.add("Second/Last response");

        final ModifyResults mockResults = context.mock(ModifyResults.class, "mockResults");
        context.checking(new Expectations() {
            {
                allowing(mockResults).getResponses();
                will(returnValue(modifyCommmand1Responses));
                allowing(mockResults).getCompletionStatus();
                will(returnValue(ModifyResults.PROCESSED_COMMAND));
            }
        });

        final String Handler1Name = "TestHandler1";
        final String modifyCommmand1 = "TRACE,something";
        final CommandHandler mockHandler = context.mock(CommandHandler.class, "wsCommandHandler");
        context.checking(new Expectations() {
            {
                allowing(mockHandler).getName();
                will(returnValue(Handler1Name));
                allowing(mockHandler).handleModify(modifyCommmand1, mockResults);
            }
        });
        final BundleContext bundleContext = context.mock(BundleContext.class, "CommandProcessorBundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });

        final CommandHandlerTest commandHandlerTest = new CommandHandlerTest();

        final ComponentContext componentContext = context.mock(ComponentContext.class, "CommandProcessorComponentContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                oneOf(componentContext).locateService(commandProcessor.getWsHandlerReferenceName(), handlerReference);
                will(returnValue(commandHandlerTest));
            }
        });

        final CommandProcessor testCommandProcessor = new CommandProcessor() {

            @Override
            protected void activate(ComponentContext cc) {

            }

            @Override
            ComponentContext getComponentContext() {
                return componentContext;
            }

            @Override
            protected String getWsHandlerReferenceName() {
                return WS_COMMAND_HANDLER_REFERENCE_NAME;
            }
        };

        // Activate the Command processor
        testCommandProcessor.activate(componentContext);

        // Create a handler holder for the test service
        CommandHandlerHolder holder = new CommandHandlerHolder(testCommandProcessor, handlerReference);
        ModifyResults deliverResults = holder.deliverCommand(modifyCommmand1);
        assertEquals(modifyCommmand1Responses, deliverResults.getResponses());

        String commandHandlerName = holder.getCommandHandlerName();
        assertSame(commandHandlerName, CommandHandlerTest.TESTCOMMANDHANDLERNAME);

        // A few additional tests to drive some ModifyResultsImpl methods
        assertEquals(false, deliverResults.responsesContainMSGIDs());
        deliverResults.setProperty(Handler1Name, "Test Properties");
        String prop = (String) deliverResults.getProperty(Handler1Name);
        assertEquals("Test Properties", prop);
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#deliverCommand(java.lang.String)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testDeliverCommandCatchBlock() {
        final String filterString = "((?i)(trace).*)";
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue(filterString));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(23L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(2023));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        final ModifyResults modifyResults = new ModifyResultsImpl();

        final String Handler1Name = "TestHandler1";
        final String modifyCommmand1 = "TRACE,something";
        final CommandHandler mockHandler = context.mock(CommandHandler.class, "wsCommandHandler");
        context.checking(new Expectations() {
            {
                allowing(mockHandler).getName();
                will(returnValue(Handler1Name));
                allowing(mockHandler).handleModify(modifyCommmand1, modifyResults);
                will(throwException(new RuntimeException("test catch")));
            }
        });
        final BundleContext bundleContext = context.mock(BundleContext.class, "CommandProcessorBundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });

        // final CommandHandlerTest commandHandlerTest = new CommandHandlerTest();
        final CommandHandlerTest commandHandlerTest = new CommandHandlerTest() {

            @Override
            public void handleModify(java.lang.String commandString, ModifyResults results) {
                throw new RuntimeException("test catch");
            }

        };

        final ComponentContext componentContext = context.mock(ComponentContext.class, "CommandProcessorComponentContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                oneOf(componentContext).locateService(commandProcessor.getWsHandlerReferenceName(), handlerReference);
                will(returnValue(commandHandlerTest));
            }
        });

        final CommandProcessor testCommandProcessor = new CommandProcessor() {

            @Override
            protected void activate(ComponentContext cc) {

            }

            @Override
            ComponentContext getComponentContext() {
                return componentContext;
            }

            @Override
            protected String getWsHandlerReferenceName() {
                return WS_COMMAND_HANDLER_REFERENCE_NAME;
            }
        };

        // Activate the Command processor
        testCommandProcessor.activate(componentContext);

        // Create a handler holder for the test service
        CommandHandlerHolder holder = new CommandHandlerHolder(testCommandProcessor, handlerReference);

        ModifyResults deliverResults = holder.deliverCommand(modifyCommmand1);

        //        [junit] ModifyResults: 
        //            [junit]     CompletionStatus = 3, 
        //            [junit]     responses = (Caught "java.lang.RuntimeException", test catch), 
        //            [junit]     ResponsesContainMSGIDs = false

        System.out.println("test driver catch path\n" + deliverResults.toString());

        assertEquals(ModifyResults.ERROR_PROCESSING_COMMAND, deliverResults.getCompletionStatus());

        boolean foundExceptionString = false;
        List<String> results = deliverResults.getResponses();
        for (String curMsg : results) {
            if (curMsg.contains("Caught \"java.lang.RuntimeException\"")) {
                foundExceptionString = true;
            }
        }
        assert (foundExceptionString);

    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#hashCode()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHash() throws Exception {
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("trace"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(20L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(200));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        CommandHandlerHolder holder = new CommandHandlerHolder(commandProcessor, handlerReference);
        assertSame(commandProcessor, holder.commandProcessor);
        assertSame(handlerReference, holder.getServiceReference());
        assertEquals("trace", holder.filterSpec);
        assertEquals(200, holder.serviceRanking);
        assertEquals(20, holder.hashCode());
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandHandlerHolder#toString()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testToString() {
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("trace"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(20L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(200));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        CommandHandlerHolder holder = new CommandHandlerHolder(commandProcessor, handlerReference);
        String results = holder.toString();
        assert (results.contains("target=") && results.contains(",serviceReference="));
    }

    /*
     * Helper Class for testing...Implements a CommandHandler.
     */
    class CommandHandlerTest implements CommandHandler {

        final static String TESTCOMMANDHANDLERNAME = "TestLoggingCommandHandler";

        /**
         * Constructor.
         */
        public CommandHandlerTest() {

        }

        /**
         * DS method for activation of this service component.
         * 
         * @param context
         */
        protected void activate(ComponentContext context) {

        }

        /**
         * DS method for deactivation of this service component.
         * 
         * @param context
         */

        protected void deactivate(ComponentContext context) {

        }

        /*
         * @see
         * com.ibm.wsspi.zos.command.processing.CommandHandler#handleModify(
         * java.lang.String, com.ibm.wsspi.zos.command.processing.ModifyResults)
         */
        @Override
        public void handleModify(java.lang.String commandString, ModifyResults results) {
            List<String> responses = new ArrayList<String>();
            responses.add("Native Logging received command");
            responses.add("Second/Last response");

            results.setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
            results.setResponses(responses);
        }

        /*
         * @see
         * com.ibm.wsspi.zos.command.processing.CommandHandler#getName()
         */
        @Override
        public String getName() {
            return TESTCOMMANDHANDLERNAME;
        }

        /**
         * @see com.ibm.wsspi.zos.command.processing.CommandHandler#getHelp()
         */
        @Override
        public List<String> getHelp() {
            List<String> responses = new ArrayList<String>();

            responses.add("Help text for CommandHandlerTest, line 1");
            responses.add("Help text for CommandHandlerTest, line 2");
            responses.add("Help text for CommandHandlerTest, line 3");
            responses.add("Help text for CommandHandlerTest, line 4");

            return responses;
        }
    };
}
