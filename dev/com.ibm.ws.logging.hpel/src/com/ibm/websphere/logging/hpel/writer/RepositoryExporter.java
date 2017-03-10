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
 * F017049-16916      8.0        08/18/2010   belyi       Clean up interface javadoc for end user
 * 723132             8.0        12/07/2011   belyi       Add ability to export sub process repositories
 */
package com.ibm.websphere.logging.hpel.writer;

import java.util.Properties;

import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord;
import com.ibm.websphere.logging.hpel.reader.ServerInstanceLogRecordList;

/**
 * Interface for exporting read repository records into another repository.
 * 
 * @ibm-api
 */
public interface RepositoryExporter {
	/**
	 * Writes header information into exported repository. This call starts export of
	 * a new server instance.
	 * 
	 * @param header Header information related to all consequent log records.
	 */
	public void storeHeader(Properties header);
	
	/**
	 * Writes header information into exported sub process repository. This call starts export
	 * of a new sub process instance. It should be called after all records of the main process
	 * were exported with {@link #storeRecord(RepositoryLogRecord)}.
	 * 
	 * @param header Header information related to all consequent log records.
	 * @param subProcess String identifier of the sub process. Use the key corresponding to the
	 * sub process used in {@link ServerInstanceLogRecordList#getChildren()} map.
	 */
	public void storeHeader(Properties header, String subProcess);
	
	
	/**
	 * Writes log record into exported repository. Calling this method before {@link #storeHeader(Properties)}
	 * will result in {@link IllegalStateException} being thrown.
	 * 
	 * @param record log record to be exported.
	 */
	public void storeRecord(RepositoryLogRecord record);
	
	/**
	 * Finishes writing exported repository and closes all open resources.
	 * Calling either {@link #storeHeader(Properties)} or {@link #storeRecord(RepositoryLogRecord)}
	 * after calling {@link #close()} will result in {@link IllegalStateException} being thrown.
	 */
	public void close();
}
