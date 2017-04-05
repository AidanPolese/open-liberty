/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin      Description
 * ------------  --------    ----------  ---------------------------------------
 *               09/05/05    schofiel    Original
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore;

public class PersistentDataEncodingException extends PersistenceException
{
    private static final long serialVersionUID = -3469937585873569733L;

    /**
     * Constructor
     * 
     * @param throwable the cause of this exception
     */
    public PersistentDataEncodingException(Throwable throwable)
    {
        super(throwable);
    }

    /**
     * Constructor
     * 
     * @param string a key to the appropriate NLS string.
     */
    public PersistentDataEncodingException(String string)
    {
        super(string);
    }

    /**
     * Constructor
     * 
     * @param string a key to the appropriate NLS string
     * @param throwable the cause of this exception
     */
    public PersistentDataEncodingException(String string, Throwable throwable)
    {
        super(string, throwable);
    }

    /**
     * Constructor
     * 
     * @param arg0 the key to the appropriate NLS string
     * @param args arguments to be inserted into the NLS string
     */
    public PersistentDataEncodingException(String arg0, Object[] args)
    {
        super(arg0, args);
    }

    /**
     * Constructor
     * 
     * @param message the key to the appropriate NLS string
     * @param inserts arguments to be inserted into the NLS string
     * @param exception the cause of this exception
     */
    public PersistentDataEncodingException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }

    /**
     * Constructor
     *
     */
    public PersistentDataEncodingException()
    {
        super();
    }
}
