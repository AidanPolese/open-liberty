package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *   251161        07/04/05   gareth    Add ObjectManager code to CMVC
 * ============================================================================
 */

/**
 * Thrown when an attempt is made to set an XID which is longer than java.lang.Short.MAX_VALUE. program.
 * 
 * @param Object throwing the exception.
 * @param int the length of the XID.
 */
public final class XIDTooLongException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 1776711044329464846L;

    protected XIDTooLongException(Object source,
                                  int length)
    {
        super(source,
              XIDTooLongException.class,
              new Object[] { new Integer(length) });
    } // XIDTooLongException().
} // class XIDTooLongException.
