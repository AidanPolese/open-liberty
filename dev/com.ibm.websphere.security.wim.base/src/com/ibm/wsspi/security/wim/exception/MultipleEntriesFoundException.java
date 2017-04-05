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
 * vmm application exception to indicate to the caller that the multiple entries are found when only one entry is expected.
 */
public class MultipleEntriesFoundException extends WIMApplicationException
{
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    /**
     * Creates the Multiple Entries Found Exception
     */
    public MultipleEntriesFoundException()
    {
        super();
    }

    /**
     * Creates the Multiple Entries Found Exception
     * @param message The message or message key of the exception.
     */
    public MultipleEntriesFoundException(String key, String message)
    {
        super(key, message);
    }

    /**
     * Creates the Multiple Entries Found Exception
     * @param message The message or message key of the exception.
     * @param cause The cause of the exception.
     */
    public MultipleEntriesFoundException(String key, String message, Throwable cause)
    {
        super(key, message, cause);
    }

    /**
     * Creates the Multiple Entries Found Exception
     * @param cause The cause of the exception.
     */
    public MultipleEntriesFoundException(Throwable cause)
    {
        super(cause);
    }
}