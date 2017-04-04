// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer;

//  PK04668    IF THE CLIENT THAT MADE THE SERVLET REQUEST GOES DOWN,THERE IS    WAS.webcontainer

/**
 * This class is a subclass of IOException used to differentiate when a client
 * prematurely terminates a connection to the server from other IOExceptions.
 */
public class ClosedConnectionException extends java.io.IOException
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3256441387271468600L;

	/**
     * Creates a new ClosedConnectionException object and invokes the 
     * super constructor.
     */
    public ClosedConnectionException()
    {
        super();
    }

    /**
     * Creates a new ClosedConnectionException object with an exception string
     * and invokes the super constructor with the string.
     *
     * @param s The message string to set in this exception
     */
    public ClosedConnectionException(String s)
    {
        super(s);
    }

    /**
     * Creates a new ClosedConnectionException object with an exception string
     * and root cause, invokes the super constructor with the string, and
     * sets initCause to the root cause.
     *
     * @param s The message string to set in this exception.
     * @param writeStatusCode The write status code.
     */

    public ClosedConnectionException(String s, Throwable t)
    {
        super(s);
        this.initCause(t);
    }

}
