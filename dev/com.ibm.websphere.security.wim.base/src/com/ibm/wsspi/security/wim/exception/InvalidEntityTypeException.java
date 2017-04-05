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
public class InvalidEntityTypeException extends WIMApplicationException
{
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    /**
     * 
     */
    public InvalidEntityTypeException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InvalidEntityTypeException(String key, String message)
    {
        super(key, message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InvalidEntityTypeException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidEntityTypeException(String key, String message, Throwable cause)
    {
        super(key, message, cause);
        // TODO Auto-generated constructor stub
    }

}
