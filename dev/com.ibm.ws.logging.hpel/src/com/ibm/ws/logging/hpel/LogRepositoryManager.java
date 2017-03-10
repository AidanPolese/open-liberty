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
 * F017049-22453      8.0        04/06/2010   mcasile     Remove setOrb and add getManagedType
 * F004324            8.0        04/21/2010   mcasile     Add in LogEventListener methods
 */
package com.ibm.ws.logging.hpel;

import java.io.File;

import com.ibm.websphere.logging.hpel.writer.LogEventListener;
import com.ibm.websphere.logging.hpel.writer.LogEventNotifier;

/**
 * Maintainer of log files in the repository.
 */
public interface LogRepositoryManager extends LogRepositoryBase {
	/**
	 * Stops all activity related to the repository management.
	 */
	public void stop();
	
	/**
	 * Removes old log files. The total sum of removed file sizes will be more than the
	 * maximum size of a single repository file unless it would require to remove all
	 * files from the repository. In this later case all files except the youngest one
	 * will be removed from the repository.<br>
	 * <b>Note:</b> this method causes removal of files independenty of the repository
	 * size retention policy or amont of space available on disk. It is designed to be
	 * issued only in response to the exception indicating out of space condition.
	 * 
	 * @return <code>true</code> if at least one file was removed from the repository.
	 */
	public boolean purgeOldFiles();
	
	/**
	 * Starts new file with the <code>timestamp</code> as a time of the first record.
	 * 
	 * @param timestamp the time in 'millis' of the first log record.
	 * @return the new File instance to write log records to.
	 */
	public File startNewFile(long timestamp);
	
	/**
	 * Checks if a new log file should be started to write the next log record.
	 * 
	 * @param total the total number of bytes in the file if the next log record is
	 * 		written into it.
	 * @param timestamp the time in 'millis' of the next log record.
	 * @return the new File instance to which log writing should be switched to or
	 * 		<code>null</code> if <code>current</code> file can still be used.
	 */
	public File checkForNewFile(long total, long timestamp);
	
	/**
	 * show the type (log, trace, textLog) that this manager is managing
	 * @return the String representing the type, values are:  trace or log or textlog
	 */
	public String getManagedType() ;
	
	/**
	 * set the type of resource (trace or log or textlog) being managed by this manager
	 * @param managedType managed type
	 */
	public void setManagedType(String managedType) ;

	/**
	 * get the LogEventNotifier associated with this process
	 * @return
	 */
	public LogEventNotifier getLogEventNotifier() ;

	/**
	 * set the process log event listener. This listener will be notified of log actions such as roll and delete
	 * @param logEventNotifier process-wide controller for notifications
	 */
	public void setLogEventNotifier(LogEventNotifier logEventNotifier) ;
	
	/**
	 * send a notification to any listeners of a log or trace file action
	 * @param eventType roll or delete
	 */
	public void notifyOfFileAction(String eventType) ;
}
