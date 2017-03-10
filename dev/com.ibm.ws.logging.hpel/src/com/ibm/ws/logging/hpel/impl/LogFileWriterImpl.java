//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2012
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
 * 738074             8.5        10/26/2012   belyi       Write records in one request
 */
package com.ibm.ws.logging.hpel.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ibm.ws.logging.hpel.LogFileWriter;

/**
 * Implementation of the {@link LogFileWriter} interface writing data to disk using {@link FileOutputStream} class.<br>
 * <b>Note:</b> For performance reasons methods of this class are thread unsafe - it expect the caller to take care of
 * only one thread using its methods at a time.
 */
public class LogFileWriterImpl extends AbstractBufferedLogFileWriter {
	private long total = 0;

	/**
	 * Creates the LogFileWriter instance writing to the file.
	 * 
	 * @param file File instance of the file to write to.
	 * @param bufferingEnabled indicator if buffering should be enabled.
	 * @throws IOException
	 */
	public LogFileWriterImpl(File file, boolean bufferingEnabled) throws IOException {
		super(file, bufferingEnabled);
	}

	public void close(byte[] tail) throws IOException {
		if (tail != null) {
			write(tail);
		}
		super.close(tail);
	}

	public void write(byte[] b) throws IOException {
		byte[] buffer = new byte[b.length + 8];
		writeLength(b.length, buffer, 0);
		System.arraycopy(b, 0, buffer, 4, b.length);
		writeLength(b.length, buffer, b.length+4);
		synchronized(fileStream) {
			fileStream.write(buffer);
		}
		total += buffer.length;
	}

	public long checkTotal(byte[] buffer, byte[] tail) {
		return total + buffer.length + tail.length + 16; // 4 bytes for each size
	}

	private void writeLength(int value, byte[] buffer, int offset) throws IOException {
		buffer[offset+3] = (byte) (value >>> 0);
		buffer[offset+2] = (byte) (value >>> 8);
		buffer[offset+1] = (byte) (value >>> 16);
		buffer[offset] = (byte) (value >>> 24);
	}

}
