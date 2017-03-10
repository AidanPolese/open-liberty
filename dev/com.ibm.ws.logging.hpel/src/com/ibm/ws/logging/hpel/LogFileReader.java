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
 * Reader of the log data from the disk.
 */
public interface LogFileReader {
	/**
	 * Returns size of the next record. It needs to be called before and after
	 * reading log record with a deserializer.
	 * 
	 * @return size of the next record in bytes.
	 * @throws IOException 
	 */ 
	int readLength() throws IOException;
	
	// File position methods
	
	/**
	 * Returns current position in the file.
	 * 
	 * @return current position in the file as a byte offset from the beginning of the file.
	 * @throws IOException 
	 */
	long getFilePointer() throws IOException;
	
	/**
	 * Sets current position in the file.
	 * 
	 * @param pos new position in the file as a byte offset from the beginning of the file.
	 * @throws IOException
	 */
	void seek(long pos) throws IOException;
	
	/**
	 * Returns the size of the underlying file.
	 * 
	 * @return file size in bytes
	 * @throws IOException
	 */
	long length() throws IOException;

	/**
	 * Reads bytes from the underlying file.
	 * 
	 * @param buffer the buffer to read bytes into.
	 * @param off the offset in be buffer of the first copied byte.
	 * @param len the number of bytes to read.
	 * @throws IOException
	 */
	void readFully(byte[] buffer, int off, int len) throws IOException;
	
	/**
	 * Closes input stream open on the underlying file.
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;
	
	/**
	 * Checks if input stream is open on the underlying file.
	 * 
	 * @return <code>true</code> if stream is open, <code>false</code> otherwise.
	 */
	boolean isOpen();
	
}
