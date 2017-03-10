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

import java.io.File;
import java.io.IOException;

/**
 * Writer of the log data to the disk.
 */
public interface LogFileWriter {
	/**
	 * Writes log record from the <code>buffer</code> into the underlying output stream.
	 * The size of the log record in bytes will be written before the log record bytes.
	 * 
	 * @param buffer array of bytes to write to disk.
	 * @throws IOException
	 */
	void write(byte[] buffer) throws IOException;
	
	/**
	 * Returns total number of bytes this writer would have written if <code>buffer</code>
	 * and <code>tail</code> are the final bytes written with it.
	 * 
	 * @param buffer array of bytes waiting to be written with the {@link #write(byte[])}
	 * method.
	 * @param tail array of bytes waiting to be sent with the final {@link #close(byte[])} call.
	 * @return the number of bytes this writer would have written after writting this buffer.
	 */
	long checkTotal(byte[] buffer, byte[] tail);
	
	/**
	 * Returns the current file associated with this writer.
	 * 
	 * @return File open by the underlying output system for writing.
	 */
	File currentFile();
	
	/**
	 * Flushes underlying output stream. 
	 * 
	 * @throws IOException 
	 */
	void flush() throws IOException;
	
	/**
	 * Flushes data and close the output stream.
	 * 
	 * @param tail Any additional bytes the user of this writer want to append to
	 * 		the file. Implementation should record it using the
	 * 		{@link #write(byte[])} method.
	 * @throws IOException 
	 */
	void close(byte[] tail) throws IOException;
	
}
