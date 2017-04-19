package com.ibm.websphere.command;

import com.ibm.websphere.command.CommandException;

// IBM Confidential OCO Source Material
// 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2000
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  FILENAME: UnavailableCompensatingCommandException.java
//
//  DESCRIPTION:
//      Used to report unavailableCompensatingcommand Exception
//
// Change Log:
//  Date       Pgmr    Defect   Description
//  --------   ------- ------   ---------------------------------
// 03/09/00   v2iicrp  75715    added constructors for distributed exception
// 01/31/05  hthomann LIDB3706-5.62 Updated serialVersionUID
//**********************************************************************

/**
 * UnavailableCompensableCommandException is thrown by the
 * getCompensatingCommand() method (in the CompensableCommand interface)
 * if it finds no compensating command to return.
 * 
 * @ibm-api
 */
public class UnavailableCompensatingCommandException
extends CommandException
{
    private static final long serialVersionUID = 8722367931685681097L;
	/**
	 * Constructor without parameters.
	 */
	public
	UnavailableCompensatingCommandException()
	{        
		super();
	}
	/**
	 * Constructor with a message.
	 * 
	 * @param message A string describing the exception.
	 */
	public
	UnavailableCompensatingCommandException(String message)
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
	public UnavailableCompensatingCommandException(String resourceBundleName,             //d75515 add
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
	public UnavailableCompensatingCommandException(String resourceBundleName,           //d75515 add
							 String resourceKey,
							 Object formatArguments[],
							 String defaultText,
							 Throwable exception)
	{
	  super(resourceBundleName, resourceKey, formatArguments, defaultText, exception);
	}
}
