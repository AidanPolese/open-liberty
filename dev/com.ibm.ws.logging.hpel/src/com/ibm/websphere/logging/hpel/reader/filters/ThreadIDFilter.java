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
package com.ibm.websphere.logging.hpel.reader.filters;

import com.ibm.websphere.logging.hpel.reader.LogRecordHeaderFilter;
import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader;

/**
 * Implementation of the {@link LogRecordHeaderFilter} interface for filtering out
 * records not written by a thread with a given thread ID.
 * 
 * @ibm-api
 */
public class ThreadIDFilter implements LogRecordHeaderFilter {
	private final int threadID;

	/**
	 * Creates a filter instance with a specified thread ID.
	 * 
	 * @param threadID ID that each record's thread ID will be compared to
	 */
	public ThreadIDFilter(int threadID) {
		this.threadID = threadID;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.LogRecordHeaderFilter#accept(com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader)
	 */
	public boolean accept(RepositoryLogRecordHeader record) {
		return threadID>=0 && record.getThreadID() == threadID;
	}

}
