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

import java.util.Properties;
import java.util.logging.LogRecord;

/**
 * Converter of log records and header information into byte arrays.
 */
public interface SerializationObject {
	/**
	 * Converts <code>header</code> into byte array.
	 * 
	 * @param header header information as a set of properties.
	 * @return byte array representing header information in a format maintained
	 * 		by this implementation.
	 */
	byte[] serializeFileHeader(Properties header);
	
	/**
	 * Converts log record into byte array.
	 * 
	 * @param record {@link LogRecord} instance to convert into bytes.
	 * @return byte array representing log record information in a format maintained
	 * 		by this implemention.
	 */
	byte[] serialize(LogRecord record);
}
