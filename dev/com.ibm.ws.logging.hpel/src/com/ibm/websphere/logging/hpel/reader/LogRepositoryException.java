//%Z% %I% %W% %G% %U% [%H% %T%]

/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Provide interface for remote access to High Performance Extensible Logs through implicit use of MBeans
 *
 * Change History:
 *
 * Reason            Version    Date        User id     Description
 * ----------------------------------------------------------------------------
 * 647124             8.0      04/20/2010   belyi       Introduce checked exception to throw in API methods.
 */
package com.ibm.websphere.logging.hpel.reader;

/**
 * Checked exception thrown in API methods in case of an error.
 * 
 * @ibm-api
 */
public class LogRepositoryException extends Exception {
	private static final long serialVersionUID = -4997826722025129896L;

	/**
	 * constructs exception with the specified message.
	 * 
	 * @param message details of the problem.
	 */
	public LogRepositoryException(String message) {
		super(message);
	}

	/**
	 * constructs exception with the specified cause.
	 * 
	 * @param cause cause for throwing this exception.
	 */
	public LogRepositoryException(Throwable cause) {
		super(cause);
	}

	/**
	 * constructs exception with the specified message and cause.
	 * 
	 * @param message details of the problem.
	 * @param cause cause for throwing this exception.
	 */
	public LogRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
