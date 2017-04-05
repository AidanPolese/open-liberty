package com.ibm.websphere.exception;

//===================================================================================
// @(#) 1.5 SERV1/ws/code/distexcep/src/com/ibm/websphere/exception/DistributedExceptionEnabled.java, WAS.distexcep, WASX.SERV1, qq1230.02 5/28/04 09:43:19 [7/25/12 20:25:34]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2014
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//===================================================================================

/**
 * Enables an exception to be treated as a distributed exception..
 * This interface should be used by an exception that is not a
 * subclass of DistributedException.
 * <p>
 * In addition to implementing the required methods, the
 * implementing class should create an attribute, a
 * DistributedExceptionInfo
 * object, in each constructor, after it has done it's other work.
 * This object will do most of the work for the methods. For all
 * of the examples in the Javadoc of the methods, it is assumed
 * that the name of this attribute is <b>distributedExceptionInfo</b>.
 * </p>
 * <p>
 * Typically, the implementor of this interface will have multiple
 * constructors. See com.ibm.websphere.DistributedException for examples.
 * </p>
 * 
 * @see com.ibm.websphere.DistributedException
 * @see com.ibm.websphere.DistributedExceptionInfo
 * @ibm-api
 */
public interface DistributedExceptionEnabled {
    /**
     * Get a specific exception in a possible chain of exceptions.
     * If there are multiple exceptions in the chain, the most recent one thrown
     * will be returned.
     * If the exception does not exist or no exceptions have been chained,
     * null will be returned.
     * <dl><dt>
     * If the DistributedExceptionInfo attribute is not null, the
     * return value can be retrieved with the following code:</dt>
     * <dd><b>distributedExceptionInfo.getException(exceptionClassName);</b></dd>
     * </dl>
     * 
     * 
     * @exception com.ibm.websphere.exception.ExceptionInstantiationException
     *                An exception occurred while trying to re-create the exception object.
     *                If this exception is thrown, the relevant information can be retrieved
     *                by using the getExceptionInfo() method followed by recursively using
     *                the getPreviousExceptionInfo() method on the DistributedExceptionInfo
     *                object.
     * 
     * @param String exceptionClassName: The class name of the specific exception.
     * @return java.lang.Throwable: The specific exception in a chain of
     *         exceptions.
     */
    public Throwable getException(String exceptionClassName) throws ExceptionInstantiationException;

    /**
     * Retrieve the exception info for this exception.
     * <dl><dt>This could be coded as:</dt>
     * <dd><b>return distributedExceptionInfo;</b></dd>
     * </dl>
     * 
     * 
     * @return com.ibm.websphere.exception.DistributedExceptionInfo
     */
    public DistributedExceptionInfo getExceptionInfo();

    /**
     * Retrieve the message for this exception.
     * 
     * <p>The following is an example of the code that should be used:
     * <dl>
     * <dt><b>if (distributedExceptionInfo != null)</b></dt>
     * <dd><b>return distributedExceptionInfo.getMessage();</b></dd>
     * <dt><b>else</b></dt>
     * <dd><b>return null</b></dd>
     * </dl>
     * </p>
     * <p>Note: During the construction of the exception and the
     * DistributedExceptionInfo object, there is one situation that results
     * in a call to this method. Since distributedExceptionInfo is still null,
     * a NullPointerException could occur if the check for null is excluded.</p>
     * 
     * @return java.lang.String
     */
    public String getMessage();

    /**
     * Get the original exception in a possible chain of exceptions.
     * If no previous exceptions have been chained, null will be returned.
     * <dl><dt>
     * If the DistributedExceptionInfo attribute is not null, the
     * return value can be retrieved with the following code:
     * </dt>
     * <dd><b>distributedExceptionInfo.getOriginalException();</b></dd>
     * </dl>
     * 
     * @exception com.ibm.websphere.exception.ExceptionInstantiationException
     *                An exception occurred while trying to re-create the exception object.
     *                If this exception is thrown, the relevant information can be retrieved
     *                by using the getExceptionInfo() method followed by recursively using
     *                the getPreviousExceptionInfo() method on the DistributedExceptionInfo
     *                object.
     * 
     * @return java.lang.Throwable: The first exception in a chain of
     *         exceptions. If no exceptions have been chained, null will be returned.
     */
    public Throwable getOriginalException() throws ExceptionInstantiationException;

    /**
     * Get the previous exception, in a possible chain of exceptions.
     * <dl><dt>
     * If the DistributedExceptionInfo attribute is not null, the
     * return value can be retrieved with the following code:
     * </dt>
     * <dd><b>distributedExceptionInfo.getPreviousException();</b></dd>
     * </dl>
     * 
     * @exception com.ibm.websphere.exception.ExceptionInstantiationException
     *                An exception occurred while trying to re-create the exception object.
     *                If this exception is thrown, the relevant information can be retrieved
     *                by using the getExceptionInfo() method.
     * 
     * @return java.lang.Throwable: The previous exception. If there was no
     *         previous exception, null will be returned.
     */
    public Throwable getPreviousException() throws ExceptionInstantiationException;

    /**
     * Print the stack trace for this exception and all chained
     * exceptions.
     * This will include the stack trace from the location where the
     * exception
     * was created, as well as the stack traces of previous
     * exceptions in the exception chain.
     * 
     * <dl><dt>
     * If the DistributedExceptionInfo attribute is not null, the
     * the following code will accomplish this:
     * </dt>
     * <dd><b>distributedExceptionInfo.printStackTrace();</b></dd>
     * </dl>
     * 
     */
    public void printStackTrace();

    /**
     * Print the exception execution stack to a print writer.
     * This will include the stack trace from the location where
     * the exception
     * was created, as well as the stack traces of previous
     * exceptions in the exception chain.
     * 
     * <dl><dt>
     * If the DistributedExceptionInfo attribute is not null, the
     * the following code will accomplish this:
     * </dt>
     * <dd><b>distributedExceptionInfo.printStackTrace(pw);</b></dd>
     * </dl>
     * 
     * @param pw java.io.PrintWriter
     */
    public void printStackTrace(java.io.PrintWriter pw);

    /**
     * <dl><dt>This method is called by DistributedExceptionInfo to retrieve and
     * save the current stack trace.</dt>
     * <dd><b>super.printStackTrace(pw)</b></dd>
     * </dl>
     * 
     * @param param java.io.PrintWriter
     */
    public void printSuperStackTrace(java.io.PrintWriter pw);
}
