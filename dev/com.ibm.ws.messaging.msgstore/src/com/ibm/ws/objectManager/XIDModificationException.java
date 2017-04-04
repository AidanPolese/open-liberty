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
 * Thrown when an attempt is made to set another XID for a transaction where it is already set.
 * 
 * @param Object throwing the exception.
 * @param byte[] the existing XID.
 * @para, byte[] the rejected XID.
 */
public final class XIDModificationException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 154584820534086970L;

    protected XIDModificationException(Object source,
                                       byte[] existingXID,
                                       byte[] rejectedXID)
    {
        super(source,
              XIDModificationException.class,
              new Object[] { existingXID,
                            rejectedXID });
    } // XIDModificationException().
} // class XIDModificationException.
