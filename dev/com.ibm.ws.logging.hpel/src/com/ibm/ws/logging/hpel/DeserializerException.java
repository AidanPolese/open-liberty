//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F001340-15950.1    8.0        09/04/2009   belyi       Initial HPEL code
 */
package com.ibm.ws.logging.hpel;

import java.io.IOException;

/**
 * Exception thrown due to an inconsistency found during reading log files.
 */
public class DeserializerException extends IOException {
	private static final long serialVersionUID = -9017682593255710738L;

	/**
	 * Creates SerializerException instance
	 * 
	 * @param msg description of the mismatch location.
	 * @param expected the value expected during deserialization.
	 * @param actual the value read during deserialization.
	 */
	public DeserializerException(String msg, String expected, String actual) {
		super(msg + " expected: " + expected + " actual: " + actual);
	}
}
