// %Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009, 2010
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

import com.ibm.websphere.logging.hpel.reader.LogQueryBean;
import com.ibm.websphere.logging.hpel.reader.LogRecordFilter;
import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord;

/**
 * filter class for all remote reader functionality.  Note that an attempt is made to do all heavy processing in the
 * constructor so that the per-record invocations go as quickly as possible.  
 */
public class MultipleCriteriaFilter implements LogRecordFilter {
	private boolean checkDate = false ;		// To support start/stop dates for single instance, this is needed
	
	private LevelFilter levelFilter = null;
	private Pattern [] includeLoggers ;	// Regular expressions gsub'd and compiled for fast compares
	private Pattern [] excludeLoggers ;
	private int [] threadIDs ;			// ThreadIds converted from hex string to ints to match record
	private Pattern [] messageContent ;	// MessageContent is seen as a string that matches some content in the record
	private long endDate ;				// endDate to check if needed
	private long startDate ;            // startDate to check if needed

	/**
	 * construct the filter for the read API
	 * @param logQueryBean bean/object with all query information
	 */
	public MultipleCriteriaFilter(LogQueryBean logQueryBean) {
		if (logQueryBean.getMinTime() != null || logQueryBean.getMaxTime() != null) {
			checkDate = true;
			startDate = logQueryBean.getMinTime() != null ? logQueryBean.getMinTime().getTime() :  0;
			endDate = logQueryBean.getMaxTime() != null ? logQueryBean.getMaxTime().getTime() : Long.MAX_VALUE;
		}
		if (logQueryBean.getMinLevel() != null || logQueryBean.getMaxLevel() != null) {
			levelFilter = new LevelFilter(logQueryBean.getMinLevel(), logQueryBean.getMaxLevel());
		}
		if (logQueryBean.getMessageContent() != null) {
			messageContent = compile(logQueryBean.getMessageContent());
		}
		if (logQueryBean.getIncludeLoggers() != null) {
			includeLoggers = compile(logQueryBean.getIncludeLoggers());
		}
		if (logQueryBean.getExcludeLoggers() != null) {
			excludeLoggers = compile(logQueryBean.getExcludeLoggers());
		}
		threadIDs = logQueryBean.getThreadIDs() ;
	}
	
	private Pattern[] compile(String[] patterns) {
		Pattern[] result = null;
		if (patterns != null) {
			result = new Pattern[patterns.length];
			for (int i=0; i<patterns.length; i++) {
				// Don't need to catch exception here since LogQueryBean won't
				// let illegal pattern to be set in its attributes.
				result[i] = LogQueryBean.compile(patterns[i]);
			}
		}
		return result;
	}

	/**
	 * filter current record per criteria passed in. Date filtering done prior to filter invocation
	 *  any failure of a criteria results in a false return (ie: don't accept record)
	 * @param record RepositoryLogRecord to filter
	 * @return true or false as to keeping this record
	 */
	public boolean accept(RepositoryLogRecord record) {
		if (checkDate) {
			if (record.getMillis() > endDate || startDate > record.getMillis())
				return false ;
		}
		if (levelFilter != null && !levelFilter.accept(record)) {
				return false ;
		}
		if (this.messageContent != null) {		// If messageContentChecking, get proper field and do contains
			String message = (record.getFormattedMessage() != null) ? record.getFormattedMessage() : record.getRawMessage() ;
			boolean match = false;
			for (Pattern pattern: this.messageContent) {
				if (pattern.matcher(message).find()) {
					match = true;
					break;
				}
			}
			if (!match) {
				return false;
			}
		}
		if (this.includeLoggers != null) {		// If includeLogger checking, look for a match to any in the array
			boolean matchIncLogger = false ;
			for (Pattern incLogger : this.includeLoggers) {
				if (incLogger.matcher(record.getLoggerName()).find()) {
					matchIncLogger = true ;
					break ;
				}
			}
			if (!matchIncLogger)
				return false ;
		}
		if (this.excludeLoggers != null) {		// If excludeLogger checking, any match means record does not meet criteria
			for (Pattern excLogger : this.excludeLoggers) {
				if (excLogger.matcher(record.getLoggerName()).find())
					return false ;
			}
		}
		if (this.threadIDs != null) {			// If thread checking, simple int equality on anything in array passes test
			for (int hexThread : this.threadIDs) {
				if (hexThread == record.getThreadID())
					return true ;
			}
			return false ;
		}
		return true ;
	}
	
}
