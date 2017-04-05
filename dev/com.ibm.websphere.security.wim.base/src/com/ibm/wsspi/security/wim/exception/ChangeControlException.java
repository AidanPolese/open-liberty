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
 */
public class ChangeControlException extends WIMApplicationException
{
    /**
     * IBM Copyright string
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    /**
     * 
     */
    public ChangeControlException()
    {
        super();
    }

    /**
     * @param message
     */
    public ChangeControlException(String key, String message)
    {
        super(key, message);
    }

    /**
     * @param cause
     */
    public ChangeControlException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ChangeControlException(String key, String message, Throwable cause)
    {
        super(key, message, cause);
    }

}
