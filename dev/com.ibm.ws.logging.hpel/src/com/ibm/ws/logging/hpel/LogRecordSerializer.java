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
 * 663081             8.0        09/09/2010   belyi       Remove LogRecord and WsLogRecord specific methods.
 */
package com.ibm.ws.logging.hpel;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Properties;

import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord;
import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader;
import com.ibm.ws.logging.object.hpel.RepositoryLogRecordImpl;

/**
 * Formater of the log records for disk storage.
 */
public interface LogRecordSerializer {
	/**
	 * Writes the file header record. Must be the first call on a new file.
	 * 
	 * @param header String properties applicable to all log records in the file.
	 * @param writer the output stream to write <code>header</code> data to.
	 * @throws IOException 
	 */
	void serializeFileHeader(Properties header, DataOutput writer) throws IOException;
	
	/**
	 * Writes a {@link RepositoryLogRecord}.
	 * 
	 * @param logRecord RepositoryLogRecord instance representing a log event.
	 * @param writer the output stream to write <code>logRecord</code> data to.
	 * @throws IOException 
	 */
	void serialize(RepositoryLogRecord logRecord, DataOutput writer) throws IOException;
	
	/**
	 * Header record indicator
	 */
	static final int HEADER = 0;
	
	/**
	 * Normal record indicator
	 */
	static final int RECORD = 1;
	
	/**
	 * Returns the eye catcher used by this implementation.
	 * 
	 * @return predevined set of bytes inserted before each record.
	 */
	int getEyeCatcherSize();
	
	/**
	 * Returns the type of the next record in the <code>reader</code>'s stream.
	 * 
	 * @param reader input stream to read header data from.
	 * @return type of the next record
	 * @throws DeserializerException if file contains unknown type
	 * @throws IOException 
	 */
	int getType(DataInput reader) throws DeserializerException, IOException;
	
	/**
	 * Reads the file header record. Must be the first call on the file.
	 * 
	 * @param reader input stream to read header data from.
	 * @return String properties applicable to all log records in the file.
	 * @throws DeserializerException on format specific inconsistency in the retrieved information.
	 * @throws IOException 
	 */
	Properties deserializeFileHeader(DataInput reader) throws DeserializerException, IOException;
	
	/**
	 * Starts reading the next log record from the <code>reader</code>.
	 * It must be the first call to start reading a record. It just reads the log record
	 * timestamp.
	 * 
	 * @param reader input stream to read data from.
	 * @return RepositoryLogRecordImpl instance with only 'millis' information initialized
	 * @throws DeserializerException on format specific inconsistency in the retrieved information.
	 * @throws IOException
	 * @see RepositoryLogRecordImpl
	 */
	RepositoryLogRecordImpl deserializeLogTime(DataInput reader) throws DeserializerException, IOException;
	
	/**
	 * Reads Level and ThreadId values into <code>logRecord</code> instance. These fields should correspond to getter
	 * methods of the {@link RepositoryLogRecordHeader} interface.
	 * It must be the second call (issued after {@link #deserializeLogTime(DataInput)}) to continue reading the record.
	 * 
	 * @param logRecord the RepositoryLogRecordImpl instance previously return from the {@link #deserializeLogTime(DataInput)} call.
	 * @param reader input stream to read data from.
	 * @throws DeserializerException on format specific inconsistency in the retrieved information.
	 * @throws IOException
	 * @see RepositoryLogRecordImpl
	 */
	void deserializeLogHead(RepositoryLogRecordImpl logRecord, DataInput reader) throws DeserializerException, IOException;
	
	/**
	 * Reads the rest of the <code>logRecord</code> information from the <code>reader</code>.
	 * It must be the third call (issued after {@link #deserializeLogHead(RepositoryLogRecordImpl, DataInput)}) to finish reading the record. 
	 * 
	 * @param logRecord the RepositoryLogRecordImpl instance originally return from the {@link #deserializeLogTime(DataInput)} call.
	 * @param reader input stream to read data from.
	 * @throws DeserializerException on format specific inconsistency in the retrieved information.
	 * @throws IOException 
	 * @see RepositoryLogRecordImpl
	 */
	void deserializeLogRecord(RepositoryLogRecordImpl logRecord, DataInput reader) throws DeserializerException, IOException;
	
	/**
	 * Find the first eyeCatcher in the <code>buffer</code> region.
	 * 
	 * @param buffer the byte array to check for the eyeCatcher.
	 * @param off the offset in the <code>buffer</code> of the region to check.
	 * @param len the length of the region to check.
	 * @return offset of the first eyeCatcher byte in the buffer.
	 */
	int findFirstEyeCatcher(byte[] buffer, int off, int len);
	
	/**
	 * Find the last eyeCatcher in the <code>buffer</code> region.
	 * 
	 * @param buffer the byte array to check for the eyeCatcher.
	 * @param off the offset in the <code>buffer</code> of the region to check.
	 * @param len the length of the region to check.
	 * @return offset of the first eyeCatcher byte in the buffer.
	 */
	int findLastEyeCatcher(byte[] buffer, int off, int len);
}
