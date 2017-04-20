package com.ibm.websphere.command;

import com.ibm.websphere.command.CommandException;

// IBM Confidential OCO Source Material
// 5639-D57 (C) COPYRIGHT International Business Machines Corp.2000
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  FILENAME: UnauthorizedAccessException
//
//  DESCRIPTION:
//      Used to report Unauthorized access exception
//
// Change Log:
//  Date       Pgmr           Defect   Description
//  --------   -------------- ------   ---------------------------------------
//  03/09/00   v2iicrp        75515    Javadoc updates
//  03/09/00   v2iicrp		  75715    added Distributed exception constructors.                                       
//  01/31/05  hthomann LIDB3706-5.62 Updated serialVersionUID
//*******************************************************************************/


/**
 * UnauthorizedAccessException is thrown when attempting to execute
 * a command without access authorization. 
 * 
 * @ibm-api
 */
public class UnauthorizedAccessException
extends CommandException
{
    private static final long serialVersionUID = -7699562712499253283L;
	/**
	 * Constructor without parameters.
	 */
	public
	UnauthorizedAccessException()
	{        
		super();
	}
	/**
	 * Constructor with a message.
	 * 
	 * @param message A string describing the exception.
	 */
	public
	UnauthorizedAccessException(String message)
	{
		super(message);
	}
        /**
	 * Constructor with information for localizing messages.
	 *
	 * @param resourceBundleName The name of resource bundle
	 *        that will be used to retrieve the message 
	 *        for getMessage() method. 
	 * @param resourceKey The key in the resource bundle that
	 *        will be used to select the specific message that is 
	 *        retrieved for the getMessage() method. 
	 * @param formatArguments The arguments to be passed to
	 *        the MessageFormat class to act as replacement variables
	 *        in the message that is retrieved from the resource bundle.
	 *        Valid types are those supported by MessageFormat. 
	 * @param defaultText The default message that will be
         *        used by the getMessage() method if the resource bundle or the
         *        key cannot be found.
	 */
	public UnauthorizedAccessException(String resourceBundleName,             //d75515 add
							 String resourceKey,
							 Object formatArguments[],
							 String defaultText)
	{
	  super(resourceBundleName, resourceKey, formatArguments, defaultText);
	}
	/**
	 * Constructor with information for localizing messages and an exception
         * to chain.
	 *
	 * @param resourceBundleName The name of resource bundle
	 *        that will be used to retrieve the message 
	 *        for getMessage() method. 
	 * @param resourceKey The key in the resource bundle that
	 *        will be used to select the specific message
	 *        retrieved for the getMessage() method. 
	 * @param formatArguments The arguments to be passed to
	 *        the MessageFormat class to act as replacement variables
	 *        in the message that is retrieved from the resource bundle.
	 *        Valid types are those supported by MessageFormat. 
	 * @param defaultText The default message that will be
         *        used by the getMessage() method if the resource bundle or the
         *        key cannot be found.
	 * @param exception The exception that is to be chained.  
	 */
	public UnauthorizedAccessException(String resourceBundleName,           //d75515 add
							 String resourceKey,
							 Object formatArguments[],
							 String defaultText,
							 Throwable exception)
	{
	  super(resourceBundleName, resourceKey, formatArguments, defaultText, exception);
	}
}
