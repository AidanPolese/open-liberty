package com.ibm.ws.sib.msgstore;
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
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * 164396          22/01/04 gareth   Informix support
 * 189573          05/02/04 gareth   Add NLS support to transaction code
 * LIDB3706-5.241  19/01/05 gareth   Add Serialization support
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

/**
 * This exception represents a failure to restore an XID from 
 * our datastore so it is a SevereException as we need to try 
 * to ensure data integrity.
 */
public class XidParsingException extends SevereMessageStoreException
{
    private static final long serialVersionUID = -8722325695588636013L;

    public XidParsingException()
    {
        super();
    }

    public XidParsingException(String message)
    {
        super(message);
    }

    public XidParsingException(Throwable exception)
    {
        super(exception);
    }

    public XidParsingException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public XidParsingException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public XidParsingException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}