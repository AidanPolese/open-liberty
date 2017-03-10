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

import java.util.regex.Pattern;

import com.ibm.websphere.logging.hpel.reader.LogRecordFilter;
import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord;

/**
 * Implementation of the {@link LogRecordFilter} interface for filtering out
 * records not written by a logger with a matching name.
 * 
 * @ibm-api
 */
public class LoggerNameFilter implements LogRecordFilter {
	private final Pattern pattern;
	
	/**
	 * Creates a filter instance for matching logger names using a specified regular expression.
	 * 
	 * 
 	 * @param namePattern regular expression {@link Pattern} that each record's
 	 *                    logger name will be compared to
	 */
	public LoggerNameFilter(String namePattern) {
		this.pattern = Pattern.compile(namePattern==null ? ".*" : namePattern);
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.LogRecordFilter#accept(com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord)
	 */
	public boolean accept(RepositoryLogRecord record) {
		return pattern.matcher(record.getLoggerName()).find();
	}

}
