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
 * Reason          Date   Origin       Description
 * --------------- ------ --------     --------------------------------------------
 *                 030203 van Leersum  Original
 * d170072         062603 kschloss     Altered printStack trace to print nextException()
 *                                     for nested SQLExceptions
 * 175771          091003 pradine      Set trowable.initCause() for nested SQLExceptions
 * 182068.5        200104 schofiel     Add constructor to ease use of externalised text messages
 * 191800          240204 pradine      Add NLS support to the persistence layer
 * 235386          061004 pradine      Fix message store retry behaviour
 * 238453          111004 pradine      Check for existing cause
 * LIDB3706-5.241  190105  gareth      Add Serialization support
 * 260055          240305 pradine      Rationalize retry logic
 * 296993          100805 pradine      Need to support new StaleConnectionException mappings
 * 353911          310306 pradine      SVT:SVC: Better msg when ME can't connect to datasource
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore;

import java.sql.SQLException;

/**
 * Wrapper for exceptions thrown by the persistence layer of the message store.
 * 
 * @author drphill
 * @author pradine
 */
public class PersistenceException extends MessageStoreException
{
    private static final long serialVersionUID = -7730683559279671494L;

    /**
     * Constructor
     * 
     * @param throwable the cause of this exception
     */
    public PersistenceException(Throwable throwable)
    {
        super(throwable);

        testForSQLException(throwable);
    }

    /**
     * Constructor
     * 
     * @param string a key to the appropriate NLS string.
     */
    public PersistenceException(String string)
    {
        super(string);
    }

    /**
     * Constructor
     * 
     * @param string a key to the appropriate NLS string
     * @param throwable the cause of this exception
     */
    public PersistenceException(String string, Throwable throwable)
    {
        super(string, throwable);

        testForSQLException(throwable);
    }

    /**
     * Constructor
     * 
     * @param arg0 the key to the appropriate NLS string
     * @param args arguments to be inserted into the NLS string
     */
    public PersistenceException(String arg0, Object[] args)
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
    public PersistenceException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);

        testForSQLException(exception);
    }

    /**
     * Constructor
     *
     */
    public PersistenceException()
    {
        super();
    }

    /**
     * SQLExceptions use {@link java.sql.SQLException#getNextException},
     * instead of {@link java.lang.Throwable#getCause}. This method sets
     * the {@link java.lang.Throwable#initCause} on the SQLException so
     * that getCause can then be used.
     *
     */
    private void testForSQLException(Throwable throwable)
    {
        try {
            while (throwable instanceof SQLException && throwable.getCause() == null) {
                SQLException cause = ((SQLException) throwable).getNextException();
                throwable.initCause(cause);
                throwable = cause;
            }
        }
        catch (Exception e) {
            //No FFDC Code Needed.
        }
    }
}
