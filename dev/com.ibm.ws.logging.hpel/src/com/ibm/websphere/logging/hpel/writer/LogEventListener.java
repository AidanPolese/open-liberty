// %Z% %I% %W% %G% %U% [%H% %T%]
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
 * F004324            8.0        04/20/2010   mcasile     Initial code
 */
package com.ibm.websphere.logging.hpel.writer;

import java.util.Date;

/**
 * Interface for event listener including eNums on types.  The listener will implement the method and be called any time a log
 * event occurs. 
 *
 * @ibm-api
 */
public interface LogEventListener {
	public static final String EVENTTYPEROLL = "WLNEventRoll" ;
	public static final String EVENTTYPEDELETE = "WLNEventDelete" ;
	public static final String REPOSITORYTYPELOG = "WLNLog" ;
	public static final String REPOSITORYTYPETRACE = "WLNTrace" ;
	public void onLogFileAction(String eventType, String repositoryType, Date dateOldestLogRecord) ;
}