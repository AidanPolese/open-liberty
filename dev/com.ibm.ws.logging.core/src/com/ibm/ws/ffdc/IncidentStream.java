/*
 * ============================================================================
 * @start_prolog@
 * Version: @(#) 1.2 SERV1/ws/code/ras.lite/src/com/ibm/ws/ffdc/IncidentStream.java, WAS.ras.lite, WASX.SERV1, uu0827.36 06/11/10 02:40:57 [7/9/08 15:03:06]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08  (C) Copyright IBM Corp. 2006
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 334159          051219 vaughton Original
 * SIB0048b.cli.1  060922 mnuttall Updated for WAS70.SIB
 *                 061031 vaughton Prep SERV1 version
 * ============================================================================
 */

package com.ibm.ws.ffdc;

public interface IncidentStream {

    public void write(String text, boolean value);

    public void write(String text, byte value);

    public void write(String text, char value);

    public void write(String text, short value);

    public void write(String text, int value);

    public void write(String text, long value);

    public void write(String text, float value);

    public void write(String text, double value);

    public void write(String text, String value);

    public void write(String text, Object value);

    public void introspectAndWrite(String text, Object value);

    public void introspectAndWrite(String text, Object value, int depth);

    public void introspectAndWrite(String text, Object value, int depth, int maxBytes);

    public void writeLine(String text, boolean value);

    public void writeLine(String text, byte value);

    public void writeLine(String text, char value);

    public void writeLine(String text, short value);

    public void writeLine(String text, int value);

    public void writeLine(String text, long value);

    public void writeLine(String text, float value);

    public void writeLine(String text, double value);

    public void writeLine(String text, String value);

    public void writeLine(String text, Object value);

    public void introspectAndWriteLine(String text, Object value);

    public void introspectAndWriteLine(String text, Object value, int depth);

    public void introspectAndWriteLine(String text, Object value, int depth, int maxBytes);
}

// End of file
