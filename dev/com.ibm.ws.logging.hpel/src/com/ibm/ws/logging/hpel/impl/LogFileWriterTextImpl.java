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
package com.ibm.ws.logging.hpel.impl;

import java.io.File;
import java.io.IOException;

/**
 * Implementation of the buffered log writer for writting in text format.
 */
public class LogFileWriterTextImpl extends AbstractBufferedLogFileWriter {
	private long total = 0;
	
	/**
	 * Creates the LogFileWriter instance writing to the text file.
	 * 
	 * @param file File instance of the file to write to.
	 * @param bufferingEnabled indicator if buffering should be enabled.
	 * @throws IOException
	 */
	public LogFileWriterTextImpl(File file, boolean bufferingEnabled) throws IOException {
		super(file, bufferingEnabled);
	}

	public long checkTotal(byte[] buffer, byte[] tail) {
		return total + buffer.length;
	}

	public void write(byte[] buffer) throws IOException {
		synchronized(fileStream) {
			fileStream.write(buffer);
		}
		total += buffer.length;
	}

}
