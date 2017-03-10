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
package com.ibm.websphere.logging.hpel.reader;

/**
 * A filter to select log records based on fields available from the {@link RepositoryLogRecordHeader}.
 * 
 * @ibm-api
 */
public interface LogRecordHeaderFilter {
	/**
	 * Checks if record should be accepted into the list.
	 * 
	 * @param record log record header to check
	 * @return <code>true</code> if record should be included in the list;
	 * 			<code>false</code> otherwise.
	 */
	boolean accept(RepositoryLogRecordHeader record);
}
