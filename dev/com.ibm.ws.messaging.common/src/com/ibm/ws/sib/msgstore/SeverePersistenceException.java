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
 * Reason     Date        Origin       Description
 * ---------- ----------- --------     --------------------------------------------
 *            11-Mar-2004 schofiel     Creation
 * LIDB3706-5.241  19/01/05  gareth   Add Serialization support
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore;

/**
 * This exception is thrown by the Persistence Layer when it encounters
 * an error which should result in the Messaging Engine being stopped.
 */
public class SeverePersistenceException extends PersistenceException
{
    private static final long serialVersionUID = 3894291004132777117L;

    /**
     * Constructor
     * 
     * @param throwable the cause of this exception
     */
    public SeverePersistenceException(Throwable throwable)
    {
        super(throwable);
    }

    /**
     * Constructor
     * 
     * @param string a key to the appropriate NLS string.
     */
    public SeverePersistenceException(String string)
    {
        super(string);
    }

    /**
     * Constructor
     * 
     * @param string a key to the appropriate NLS string
     * @param throwable the cause of this exception
     */
    public SeverePersistenceException(String string, Throwable throwable)
    {
        super(string, throwable);
    }

    /**
     * Constructor
     * 
     * @param arg0 the key to the appropriate NLS string
     * @param args arguments to be inserted into the NLS string
     */
    public SeverePersistenceException(String arg0, Object[] args)
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
    public SeverePersistenceException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }

    /**
     * Constructor
     *
     */
    public SeverePersistenceException()
    {
        super();
    }
}
