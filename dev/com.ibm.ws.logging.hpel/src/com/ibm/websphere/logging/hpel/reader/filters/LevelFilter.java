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

import java.util.logging.Level;

import com.ibm.websphere.logging.hpel.reader.LogRecordHeaderFilter;
import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader;

/**
 * Implementation of the {@link LogRecordHeaderFilter} interface for filtering out
 * records not falling into a specified Level range.
 * 
 * @ibm-api
 */
public class LevelFilter implements LogRecordHeaderFilter {
	private final int minLevel;
	private final int maxLevel;
	
	/**
	 * Creates a filter instance using integer values as the level range.
	 * 
	 * These level integers are as defined in the java.util.logging.Level class.
	 * 
	 * @param minLevel lower boundary of the level range.
	 * @param maxLevel upper boundary of the level range.
	 * @see java.util.logging.Level
	 */
	public LevelFilter(int minLevel, int maxLevel){
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}

	/**
	 * Creates a filter instance with a specified Level range.
	 * 
	 * @param minLevel lower boundary of the level range. Value <code>null</code> means that lower boundary won't be checked.
	 * @param maxLevel upper boundary of the level range. Value <code>null</code> means that upper boundary won't be checked.
	 */
	public LevelFilter(Level minLevel, Level maxLevel){
		this.minLevel = minLevel==null ? Level.ALL.intValue() : minLevel.intValue();
		this.maxLevel = maxLevel==null ? Level.OFF.intValue() : maxLevel.intValue();
	}

	public boolean accept(RepositoryLogRecordHeader record) {	
		Level recordLevel = record.getLevel();
	
		if(recordLevel.intValue()>= minLevel && recordLevel.intValue() <= maxLevel){
			return true;
		}
		
		return false;
	}

}
