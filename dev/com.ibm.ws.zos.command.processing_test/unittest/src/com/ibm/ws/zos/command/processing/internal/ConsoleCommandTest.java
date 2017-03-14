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
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class ConsoleCommandTest {

    ConsoleCommand consoleCmd;

    private int forTestCmd = 0;
    private static int TEST_ERRORCODE = 0x1234567;
    private static int TEST_CONSOLEID = 0x0100004;
    private static String TEST_CONSOLE_NAME = "C3E2SY1 ";
    private static long TEST_CART = 0xC9E2C6C83B56FD66l;
    private static String TEST_MODIFYCMD = "DISPLAY,SOMEWORK,FOR,ME";

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
    public void setUp() throws Exception {
        // Build test byte array
        byte[] localCommand = new byte[256];
        int commandType = CommandProcessor.CIA_COMMANDTYPE_MODIFY;
        forTestCmd++;
        if (forTestCmd > 4) {
            commandType = CommandProcessor.CIA_COMMANDTYPE_STOP;
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

        consoleCmd = new ConsoleCommand(localCommand);

        System.out.println("Using the following console command for tests\n" + consoleCmd.toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        consoleCmd = null;
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.ConsoleCommand#getCommandType()}.
     */
    @Test
    public void test_getCommandType() {

        // get the type --  test for modify
        int localCmdType = consoleCmd.getCommandType();
        assertEquals(Integer.valueOf(localCmdType), Integer.valueOf(CommandProcessor.CIA_COMMANDTYPE_MODIFY));
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.ConsoleCommand#getErrorCode()}.
     */
    @Test
    public void test_getErrorCode() {
        // get the errorCode --  test for TEST_ERRORCODE
        int localErrorCode = consoleCmd.getErrorCode();
        assertEquals(Integer.valueOf(localErrorCode), Integer.valueOf(TEST_ERRORCODE));
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.ConsoleCommand#getConsoleID()}.
     */
    @Test
    public void test_getConsoleID() {
        int localConsID = consoleCmd.getConsoleID();
        assertEquals(Integer.valueOf(localConsID), Integer.valueOf(TEST_CONSOLEID));
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.ConsoleCommand#getConsoleName()}.
     */
    @Test
    public void test_getConsoleName() {
        String localConsoleName = consoleCmd.getConsoleName();
        assertTrue("Retrieved Console Name", TEST_CONSOLE_NAME.equals(localConsoleName));
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.ConsoleCommand#getCart()}.
     */
    @Test
    public void test_getCart() {
        long localCART = consoleCmd.getCart();
        assertEquals(Long.valueOf(localCART), Long.valueOf(TEST_CART));
    }

    /**
     * Test method for {@link com.ibm.ws.zos.command.processing.internal.ConsoleCommand#getCommandString()}.
     */
    @Test
    public void test_getCommandString() {
        // get the CommandString --  test for 0x1234567
        String localCmdString = consoleCmd.getCommandString();
        assertTrue("Retrieved Cmd", TEST_MODIFYCMD.equals(localCmdString));
    }
}
