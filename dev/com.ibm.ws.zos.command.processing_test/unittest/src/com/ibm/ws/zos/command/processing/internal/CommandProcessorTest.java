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

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import test.common.SharedOutputManager;

import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;
import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.kernel.zos.NativeMethodManager;

public class CommandProcessorTest {

    private static SharedOutputManager outputMgr;

    private static int TEST_ERRORCODE = 0x1234567;
    private static int TEST_CONSOLEID = 0x0100004;
    private static long TEST_CART = 0xC9E2C6C83B56FD66l;

    final Mockery context = new JUnit4Mockery();

    class TestCommandProcessor extends CommandProcessor {

        @Override
        protected long ntv_getIEZCOMReference() {
            return 0x0000000000FCBDF0L;
        }

        @Override
        protected byte[] ntv_getCommand(long iezcomm) {

            // Build test byte array
            byte[] localCommand = new byte[256];
            int commandType = CommandProcessor.CIA_COMMANDTYPE_MODIFY;

            if (iezcomm == 0x00010002l) {
                commandType = CommandProcessor.CIA_COMMANDTYPE_MODIFY;
            } else if (iezcomm == 0x00010003l) {
                commandType = CommandProcessor.CIA_COMMANDTYPE_STOP;
            } else if (iezcomm == 0x00010004l) {
                commandType = CommandProcessor.CIA_COMMANDTYPE_UNKNOWN;
            } else if (iezcomm == 0x00010005l) {
                commandType = CommandProcessor.CIA_COMMANDTYPE_ENDING;
            } else if (iezcomm == 0x00010006l) {
                commandType = CommandProcessor.CIA_COMMANDTYPE_ERROR;
            }

            ByteBuffer buf = ByteBuffer.allocate(256);
            // buf.position(0);
            buf.putInt(ConsoleCommand.I_cia_commandType, commandType);
            int errorCode = TEST_ERRORCODE;
            buf.putInt(ConsoleCommand.I_cia_errorCode, errorCode);
            buf.putInt(ConsoleCommand.I_cia_consoleID, TEST_CONSOLEID);

            // C3F3C5F2 E2E8F140 = "C3E2SY1 " (TEST_CONSOLE_NAME)
            buf.putInt(ConsoleCommand.I_cia_consoleName, 0xC3F3C5F2);
            buf.putInt(ConsoleCommand.I_cia_consoleName + 4, 0xE2E8F140);

            // real CART value I got from SDSF-> /f bbgzsrv,....
            // c9e2c6c83b56fd66
            buf.putLong(ConsoleCommand.I_cia_commandCART, TEST_CART);
            //   int localCART1 = 0xC9E2C6C8;
            //   int localCART2 = 0x3B56FD66;
            //   buf.putInt(ConsoleCommand.I_cia_commandCART, localCART1);
            //   buf.putInt(ConsoleCommand.I_cia_commandCART + 4, localCART2);

            int cmdlen = 0x00000019;
            buf.putInt(ConsoleCommand.I_cia_commandRestOfCommandLength, cmdlen);

            // Add quote around command, beginning
            buf.put(ConsoleCommand.I_cia_commandRestOfCommand, (byte) 0x7D);

            //C4C9E2D7 D3C1E86B E2D6D4C5 E6D6D9D2 *DISPLAY,SOMEWORK*
            //6BC6D6D9 6BD4C500 31044876 E000C000 *,FOR,ME.....\.{.*
            int cmd1 = 0xC4C9E2D7;
            int cmd2 = 0xD3C1E86B;
            int cmd3 = 0xE2D6D4C5;
            int cmd4 = 0xE6D6D9D2;
            int cmd5 = 0x6BC6D6D9;
            int cmd6 = 0x6BD4C57D;
            buf.putInt(ConsoleCommand.I_cia_commandRestOfCommand + 0 + 1, cmd1);
            buf.putInt(ConsoleCommand.I_cia_commandRestOfCommand + 4 + 1, cmd2);
            buf.putInt(ConsoleCommand.I_cia_commandRestOfCommand + 8 + 1, cmd3);
            buf.putInt(ConsoleCommand.I_cia_commandRestOfCommand + 12 + 1, cmd4);
            buf.putInt(ConsoleCommand.I_cia_commandRestOfCommand + 16 + 1, cmd5);
            buf.putInt(ConsoleCommand.I_cia_commandRestOfCommand + 20 + 1, cmd6);

            // Add quote around command, end (is in the last byte of the last "int" written)
            // buf.put(ConsoleCommand.I_cia_commandRestOfCommand + 24 + 1, (byte) 0x7D);

            buf.rewind();
            buf.get(localCommand, 0, 57);

            return localCommand;

        }

        /**
         * Call to native code to stop listening for operator commands
         */
        @Override
        protected void ntv_stopListeningForCommands() {
            return;
        }

        @Override
        protected int ntv_issueCommandResponse(byte[] response, long cart, int consid) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("ntv_issueCommandResponse:").append(new String(response, "Cp1047"));
                sb.append(", cart:").append(Long.toHexString(cart));
                sb.append(", consid:").append(Integer.toHexString(consid));
                System.out.println(sb);
            } catch (UnsupportedEncodingException e) {
            }
            return 0;
        }
    };

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        outputMgr = SharedOutputManager.getInstance();
        outputMgr.captureStreams();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() {
        // Restore stdout and stderr to normal behavior
        outputMgr.restoreStreams();
    }

    @After
    public void tearDown() {
        // Clear the output generated after each method invocation
        outputMgr.resetStreams();
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandProcessor#processCommand(byte[])}.
     */
    @Test
    public void testSetServices() {
        @SuppressWarnings("unchecked")
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("((?i)(display).*)"));
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
                oneOf(componentContext).locateService(CommandProcessor.WS_COMMAND_HANDLER_REFERENCE_NAME, handlerReference);
                will(returnValue(mockHandler));
            }
        });

        CommandProcessor cp = new TestCommandProcessor();

        // Test service setting and unsetting
        cp.setWsCommandHandler(handlerReference);
        assertTrue("ServiceReference in serviceReferenceMap",
                   cp.serviceReferenceMap.containsKey(handlerReference));
        cp.unsetWsCommandHandler(handlerReference);
        assertTrue("ServiceReference in serviceReferenceMap",
                   !cp.serviceReferenceMap.containsKey(handlerReference));

        final NativeMethodManager mockNativeMethodManager = context.mock(NativeMethodManager.class);
        cp.setNativeMethodManager(mockNativeMethodManager);
        assertTrue(cp.nativeMethodManager == mockNativeMethodManager);
        cp.unsetNativeMethodManager(mockNativeMethodManager);
        assertTrue(null == cp.nativeMethodManager);

        final LibertyProcess mockCommandLine = context.mock(LibertyProcess.class);
        cp.setKernelCommandLine(mockCommandLine);
        assertTrue(cp.cmdLine == mockCommandLine);
        cp.unsetKernelCommandLine(mockCommandLine);
        assertTrue(null == cp.cmdLine);

    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandProcessor#processCommand(byte[])}.
     */
    @Test
    public void testProcessCommand() {
        @SuppressWarnings("unchecked")
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("((?i)(display).*)"));
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
                oneOf(componentContext).locateService(CommandProcessor.WS_COMMAND_HANDLER_REFERENCE_NAME, handlerReference);
                will(returnValue(mockHandler));
            }
        });

        final WsLocationAdmin locationAdmin = context.mock(WsLocationAdmin.class, "locationAdmin");
        context.checking(new Expectations() {
            {
                allowing(locationAdmin).getServerName();
                will(returnValue("defaultServer"));
            }
        });

        CommandProcessor cp = new TestCommandProcessor();
        cp.setLocationAdmin(locationAdmin);

        // Get a modify command 
        byte[] command = cp.ntv_getCommand(0x00010002l);
        assertTrue("Got command", (command != null));

        // Testing modify path
        cp.processCommand(command);

        // get a Stop command
        command = cp.ntv_getCommand(0x00010003l);
        cp.processCommand(command);

        // get a Error command
        command = cp.ntv_getCommand(0x00010006l);
        cp.processCommand(command);

    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandProcessor#processCommand(byte[])}.
     */
    @Test
    public void testDeliverCommandToHandlers_error() {
        @SuppressWarnings("unchecked")
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("((?i)(display).*)"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(22L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(null));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        final String Handler1Name = "TestHandler1";
        final CommandHandler mockHandler = context.mock(CommandHandler.class, "mockHandler");
        context.checking(new Expectations() {
            {
                allowing(mockHandler).getName();
                will(returnValue(Handler1Name));
            }
        });

        final BundleContext bundleContext = context.mock(BundleContext.class, "bundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });
        final ComponentContext componentContext = context.mock(ComponentContext.class, "componentContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                allowing(componentContext).locateService(CommandProcessor.WS_COMMAND_HANDLER_REFERENCE_NAME, handlerReference);
                will(returnValue(mockHandler));
            }
        });

        final CommandProcessor cp = new TestCommandProcessor() {

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

        CommandHandlerHolder chh = new CommandHandlerHolder(cp, handlerReference) {
            @Override
            public ModifyResults deliverCommand(java.lang.String modifyCommmand) {
                ModifyResultsImpl results = new ModifyResultsImpl();
                results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
                try {
                    throw new RuntimeException("throwing RuntimeException");
                } catch (Throwable t) {
                    results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
                    List<String> response = new ArrayList<String>();
                    response.add("BBGZ0001E: CommandHandlerHolder Caught \"" + t.getClass().getName() + "\", " + t.getMessage());
                    response.add("BBGZ0001E: Last line of responses");
                    results.setResponses(response);
                    results.setResponsesContainMSGIDs(true);
                }

                return results;
            }
        };

        chh.targetHandlerName = Handler1Name;

        // Handler into service map    
        cp.serviceReferenceMap.put(handlerReference, chh);

        // Get a modify command 
        byte[] command = cp.ntv_getCommand(0x00010002l);
        assertTrue("Got command", (command != null));

        ConsoleCommand consoleCommand = null;
        try {
            consoleCommand = new ConsoleCommand(command);
        } catch (UnsupportedEncodingException e) {
        }

        System.out.println("before cp.deliverCommandToHandlers");
        // Testing modify path
        int rc = cp.deliverCommandToHandlers(consoleCommand);
        System.out.println("rc for deliverCommandToHandlers\n" + rc);
        assertTrue("deliverCommandToHandlers successful", (rc == ModifyResults.ERROR_PROCESSING_COMMAND));
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandProcessor#processCommand(byte[])}.
     */
    @Test
    public void testDeliverCommandToHandlersUnknown() {
        @SuppressWarnings("unchecked")
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("((?i)(display).*)"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(22L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(null));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        final String Handler1Name = "TestHandler1";
        final CommandHandler mockHandler = context.mock(CommandHandler.class, "mockHandler");
        context.checking(new Expectations() {
            {
                allowing(mockHandler).getName();
                will(returnValue(Handler1Name));
            }
        });

        final BundleContext bundleContext = context.mock(BundleContext.class, "bundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });
        final ComponentContext componentContext = context.mock(ComponentContext.class, "componentContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                allowing(componentContext).locateService(CommandProcessor.WS_COMMAND_HANDLER_REFERENCE_NAME, handlerReference);
                will(returnValue(mockHandler));
            }
        });

        final CommandProcessor cp = new TestCommandProcessor() {

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

        CommandHandlerHolder chh = new CommandHandlerHolder(cp, handlerReference) {
            @Override
            public ModifyResults deliverCommand(java.lang.String modifyCommmand) {
                ModifyResultsImpl results = new ModifyResultsImpl();
                results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);

                List<String> response = new ArrayList<String>();
                response.add("BBGZ0001W: CommandHandlerHolder Unknown status with Msg 1");
                response.add("BBGZ0001W: Last line of responses");
                results.setResponses(response);
                results.setResponsesContainMSGIDs(false);

                return results;
            }

            @Override
            public List<String> getHelp() {
                List<String> response = new ArrayList<String>();
                response.add("BBGZ0001W: CommandHandlerHolder help string 1");
                response.add("BBGZ0001W: Last line of help responses");

                return response;
            }
        };

        chh.targetHandlerName = Handler1Name;

        // Handler into service map    
        cp.serviceReferenceMap.put(handlerReference, chh);

        // Get a modify command 
        byte[] command = cp.ntv_getCommand(0x00010002l);
        assertTrue("Got command", (command != null));

        ConsoleCommand consoleCommand = null;
        try {
            consoleCommand = new ConsoleCommand(command);
        } catch (UnsupportedEncodingException e) {
        }

        System.out.println("before cp.deliverCommandToHandlers");
        // Testing modify path
        int rc = cp.deliverCommandToHandlers(consoleCommand);
        System.out.println("rc for deliverCommandToHandlers\n" + rc);
        assertTrue("deliverCommandToHandlers successful", (rc == ModifyResults.UNKNOWN_COMMAND));
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.CommandProcessor#processCommand(byte[])}.
     */
    @Test
    public void testDeliverCommandToHandlersProcessed() {
        @SuppressWarnings("unchecked")
        final ServiceReference<CommandHandler> handlerReference = context.mock(ServiceReference.class, "handlerReference");
        context.checking(new Expectations() {
            {
                allowing(handlerReference).getProperty(CommandHandler.MODIFY_FILTER);
                will(returnValue("((?i)(display).*)"));
                allowing(handlerReference).getProperty(Constants.SERVICE_ID);
                will(returnValue(22L));
                allowing(handlerReference).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(null));
                allowing(handlerReference).getProperty(CommandHandler.DISPLAY_HELP);
                will(returnValue("TRUE"));
            }
        });

        final String Handler1Name = "TestHandler1";
        final CommandHandler mockHandler = context.mock(CommandHandler.class, "mockHandler");
        context.checking(new Expectations() {
            {
                allowing(mockHandler).getName();
                will(returnValue(Handler1Name));
            }
        });

        final BundleContext bundleContext = context.mock(BundleContext.class, "bundleContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
            }
        });
        final ComponentContext componentContext = context.mock(ComponentContext.class, "componentContext");
        context.checking(new Expectations() {
            {
                ignoring(bundleContext);
                allowing(componentContext).getProperties();
                will(returnValue(new Hashtable<String, Object>()));
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
                allowing(componentContext).locateService(CommandProcessor.WS_COMMAND_HANDLER_REFERENCE_NAME, handlerReference);
                will(returnValue(mockHandler));
            }
        });

        final CommandProcessor cp = new TestCommandProcessor() {

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

        CommandHandlerHolder chh = new CommandHandlerHolder(cp, handlerReference) {
            @Override
            public ModifyResults deliverCommand(java.lang.String modifyCommmand) {
                ModifyResultsImpl results = new ModifyResultsImpl();
                results.setCompletionStatus(ModifyResults.PROCESSED_COMMAND);

                List<String> response = new ArrayList<String>();
                response.add("BBGZ0001W: CommandHandlerHolder success status with Msg 1");
                response.add("BBGZ0001W: Last line of responses");
                results.setResponses(response);
                results.setResponsesContainMSGIDs(false);

                return results;
            }

            @Override
            public List<String> getHelp() {
                List<String> response = new ArrayList<String>();
                response.add("BBGZ0001W: CommandHandlerHolder help string 1");
                response.add("BBGZ0001W: Last line of help responses");

                return response;
            }
        };

        chh.targetHandlerName = Handler1Name;

        // Handler into service map    
        cp.serviceReferenceMap.put(handlerReference, chh);

        // Get a modify command 
        byte[] command = cp.ntv_getCommand(0x00010002l);
        assertTrue("Got command", (command != null));

        ConsoleCommand consoleCommand = null;
        try {
            consoleCommand = new ConsoleCommand(command);
        } catch (UnsupportedEncodingException e) {
        }

        System.out.println("before cp.deliverCommandToHandlers");
        // Testing modify path
        int rc = cp.deliverCommandToHandlers(consoleCommand);
        System.out.println("rc for deliverCommandToHandlers\n" + rc);
        assertTrue("deliverCommandToHandlers successful", (rc == ModifyResults.PROCESSED_COMMAND));
    }
}
