//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2011
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
 * 642471             8.0        03/10/2010   spaungam    HPEL formatter refactoring
 * 653336             8.0        05/20/2010   belyi       Accept the whole HpelFormatter in the constructor
 * F017049-16916      8.0        08/18/2010   belyi       Rename class and clean up its javadoc for end users
 * 723132             8.0        12/07/2011   belyi       Add ability to export sub process repositories
 */
package com.ibm.websphere.logging.hpel.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import com.ibm.websphere.logging.hpel.reader.HpelFormatter;
import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord;

/**
 * Implementation of the {@link RepositoryExporter} interface exporting log records
 * into a text file in Basic or Advanced WebSphere format. The method <code>storeHeader</code> must be called before
 * any records can be stored.  Each record is stored with the <code>storeRecord</code> function.  Failure to
 * follow the order will result in runtime exceptions.
 * 
 * @ibm-api
 */
public class CompatibilityRepositoryExporter implements RepositoryExporter {
	private final PrintStream out;
	private boolean closeStream = false;
	private final HpelFormatter formatter;
	private boolean isClosed = false; // value "true" indicates that exporter was already closed
	private boolean isInitialized = false; // value "true" indicates that storeHeader was issued at least once
	
	/**
	 * Creates an instance for storing records in a file in a Basic or Advanced text format.
	 * 
	 * @param outputFile    output file
	 * @param formatter     formatter to use when converting LogRecords into text
	 * @throws IOException  if an I/O error has occurred
	 */
	public CompatibilityRepositoryExporter(File outputFile, HpelFormatter formatter) throws IOException {
		this(new BufferedOutputStream(new FileOutputStream(outputFile, false)), formatter);
		closeStream = true;
	}

	/**
	 * Creates an instance for writing records into a stream in a Basic or Advanced text format.
	 * 
	 * @param out          output stream.
	 * @param formatter    formatter to use when converting LogRecords into text
	 * @see HpelFormatter
	 */
	protected CompatibilityRepositoryExporter(OutputStream out, HpelFormatter formatter) {
		this(new PrintStream(out), formatter);
	}
	
	/**
	 * Creates an instance for writing records into a stream in a Basic or Advanced text format.
	 * 
	 * @param out          output stream.
	 * @param formatter    formatter to use when converting LogRecords into text
	 * @see HpelFormatter
	 */
	public CompatibilityRepositoryExporter(PrintStream out, HpelFormatter formatter) {
		this.out = out;
		this.formatter = formatter;
	}
	
	/**
	 * flushes and closes the output stream
	 */
	public void close() {
		out.flush();
		if (closeStream) {
			out.close();
		}
		isClosed = true;
	}

	/**
	 * Stores the header properties into the output file
	 * @param header  Properties (key/value) storing header information
	 */
	public void storeHeader(Properties header) {
		storeHeader(header, null);
	}
	
	public void storeHeader(Properties header, String subProcess) {		
		if (isClosed) {
			throw new IllegalStateException("This instance of the exporter is already closed");
		}
		if (subProcess != null) {
			out.print("----------  ");
			out.print(subProcess);
			out.print("  ----------");
			out.print(formatter.getLineSeparator());
		}
		formatter.setHeaderProps(header);
		for (String headerLine: formatter.getHeader()) {
			out.print(headerLine);
			out.print(formatter.getLineSeparator()) ;
		}
		isInitialized = true;
	}

	/**
	 * Stores a RepositoryLogRecord into the proper text format
	 * @param record  RepositoryLogRecord which formatter will convert to Basic or Advanced output format
	 */
	public void storeRecord(RepositoryLogRecord record) {
		if (isClosed) {
			throw new IllegalStateException("This instance of the exporter is already closed");
		}
		if (!isInitialized) {
			throw new IllegalStateException("This instance of the exporter does not have header information yet");
		}
		String formatRecord = formatter.formatRecord(record);
		out.print(formatRecord);
		out.print(formatter.getLineSeparator());
	}

}
