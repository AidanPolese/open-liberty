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
 * 647124             8.0      04/20/2010   belyi       Introduce unchecked exception to throw in API methods.
 */
package com.ibm.websphere.logging.hpel.reader;

/**
 * Unchecked exception to wrap checked one thrown in methods without 'throws' clause.
 * 
 * @ibm-api
 */
public class LogRepositoryRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 6213833785797600508L;

	/**
	 * constructs exception with the specified cause.
	 * 
	 * @param cause underlying LogRepositoryException
	 */
	public LogRepositoryRuntimeException(LogRepositoryException cause) {
		super(cause);
	}
}
