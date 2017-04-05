/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person        Defect/Feature      Comments
 * -------      ------       --------------      --------------------------------------------------
 */
package com.ibm.wsspi.security.wim.exception;

/**
 * @author Rohan Zunzarrao
 */
public class EntityHasDescendantsException extends WIMApplicationException
{
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2014;

    /**
     * 
     */
    public EntityHasDescendantsException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public EntityHasDescendantsException(String key, String message)
    {
        super(key, message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public EntityHasDescendantsException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public EntityHasDescendantsException(String key, String message, Throwable cause)
    {
        super(key, message, cause);
        // TODO Auto-generated constructor stub
    }

}
