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

/**
 * Log repository writer with assigned log record level.
 */
public interface LogRepositoryWriter {
	/**
	 * Sets header information for this writer.
	 * 
	 * @param headerBytes header information as a byte array.
	 */
	public void setHeader(byte[] headerBytes);
	
	/**
	 * Publishes log record with this writer.
	 * 
	 * @param timestamp the time of the log record.
	 * @param bytes record information as a byte array.
	 */
	public void logRecord(long timestamp, byte[] bytes);
	
	/**
	 * Returns manager used by this writer.
	 *
	 * @return manager configured during construction of this writer.
	 */
	public LogRepositoryManager getLogRepositoryManager();
	
	/**
	 * Stops this writer and releases resources held by it.
	 */
	public void stop();
}
