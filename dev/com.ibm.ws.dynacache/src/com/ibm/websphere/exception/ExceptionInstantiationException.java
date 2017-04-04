package com.ibm.websphere.exception;

//===================================================================================
// @(#) 1.6 SERV1/ws/code/distexcep/src/com/ibm/websphere/exception/ExceptionInstantiationException.java, WAS.distexcep, WASX.SERV1, qq1230.02 2/27/05 09:18:00 [7/25/12 20:25:35]
//
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 2000,2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//===================================================================================

/**
 * Exception - ExceptionInstantiationException
 * This indicates that an exception was thrown when trying	
 * to instantiate a previous
 * exception in a chain of exceptions. The specific
 * exception can by retrieved using getPreviousException().
 * @ibm-api
 */
public class ExceptionInstantiationException extends DistributedException {

    private static final long serialVersionUID = 4996311351594139105L;

/**
 * Default constructor.
 */
public ExceptionInstantiationException() {
	super();
}
/**
 * Constructor with a message.
 * @param message java.lang.String Message text
 */
public ExceptionInstantiationException(String message) 
{
	super(message);
}
/**
 * Constructor with message text.
 * @param s java.lang.String
 */
public ExceptionInstantiationException(String resourceBundleName,
									String resourceKey,
									Object[] formatArguments,
									String defaultText) 
{
	super(resourceBundleName,resourceKey,formatArguments,defaultText,null);
}
/**
 * Constructor with message text and previous exception.
 * @param text java.lang.String
 * @param exception java.lang.Throwable
 */
public ExceptionInstantiationException(String resourceBundleName,
									String resourceKey,
									Object[] formatArguments,
									String defaultText, 
									Throwable exception)
{
	super(resourceBundleName,resourceKey,formatArguments,defaultText,exception);
}
/**
 * Constructor with a message and an exception to be chained.
 * @param message The message for this exception
 * @param exception java.lang.Throwable The exception to be chained
 */
public ExceptionInstantiationException(String message,Throwable exception) 
{
	super(message,exception);
}
/**
 * Constructor with previous exception.
 * @param exception java.lang.Throwable
 */
public ExceptionInstantiationException(Throwable exception) {
	super(exception);
}
}
