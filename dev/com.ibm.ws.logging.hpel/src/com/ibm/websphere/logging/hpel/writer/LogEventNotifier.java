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
 * F004324            8.0        04/20/2010   mcasile     Initial code
 */
package com.ibm.websphere.logging.hpel.writer;

import java.util.Date;

/**
 * Interface for visibility/dependency purposes which logging systems call to when roll or delete events occur and which
 * listeners for these events call to register.  The implementer maintains the collection of listeners and, on event
 * occurrence, notifies all listeners.
 * @ibm-spi
 */
public interface LogEventNotifier {

	/**
	 * set the oldest date based on repository type. This is generally called soon after this object is constructed as the
	 * managers are notified of the object
	 * @param oldestDate oldest date in the repository for that repository type. This may be null if manager is unable to determine oldest date
	 * @param repositoryType type of repository (log/trace)
	 */
	public abstract void setOldestDate(Date oldestDate, String repositoryType);
	
	/**
	 * record that a file action has taken place on a file type, leaving current oldest record as curOldestDate
	 * @param eventType roll or delete
	 * @param repositoryType log or trace 
	 * @param curOldestDate this will be null if it does not change the value or oldest date not determinable
	 */
	public abstract void recordFileAction(String eventType, String repositoryType, Date curOldestDate);

	/**
	 * register a new listener for log events.  This listener will be notified any time a roll or delete event occurs
	 * on a log or trace system. 
	 * @param eventListener implementer of the LogEventListener interface
	 */
	public abstract void registerListener(LogEventListener eventListener);

	/**
	 * deRegister a listener for log events.  Indicates that this listener is no longer interested in receiving
	 * log and trace events
	 * @param eventListener implementer of the LogEventListener interface
	 */
	public abstract void deRegisterListener(LogEventListener eventListener);

	/**
	 * return the oldest record of the current type. For this sample, does not differentiate. In reality later, this will forward
	 * to the appropriate repositoryManager which will calculate oldest record.
	 * @param repositoryType Log vs Trace
	 * @return Date of oldest record in the repository.  Null if manager could not determine this.
	 */
	public abstract Date getOldestLogRecordTime(String repositoryType);

}
