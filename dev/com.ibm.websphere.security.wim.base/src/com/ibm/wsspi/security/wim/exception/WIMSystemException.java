/************** Begin Copyright - Do not add comments here **************
*
 *  
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */
package com.ibm.wsspi.security.wim.exception;


/**
 * @author Ankit Jain
 * 
 * A generic vmm application exception to indicate to the caller
 * that there was a problem with the current request due to incorrect inputs
 * from the caller.
 **/
public class WIMSystemException extends WIMException
{
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    /**
     * Creates the virtual member manager system level exception.
     */
    public WIMSystemException()
    {
        super();
    }

    /**
     * Creates the WIMSystemException.
     * @param message The message or message key of the exception.
     **/
    public WIMSystemException(String key, String message)
    {
        super(key, message);
    }

     /**
     * Creates the WIMSystemException.
     * @param cause The cause of the exception.
     **/
    public WIMSystemException(Throwable cause)
    {
        super(cause);
    }

     /**
     * Creates the WIMSystemException.
     * @param cause The cause of the exception.
     **/
    public WIMSystemException(String key, String message, Throwable cause)
    {
        super(key, message, cause);
    }

}