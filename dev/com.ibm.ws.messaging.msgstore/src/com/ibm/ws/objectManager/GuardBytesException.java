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
 * Thrown when an invalid guard byte is found.
 */
public final class GuardBytesException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 8186135736870686061L;

    /**
     * StateErrorException.
     * 
     * @param Object which is throwing this StateErrorException.
     * @param Object protected by the guard bytes.
     */
    protected GuardBytesException(Object source, Object target)
    {
        super(source,
              GuardBytesException.class
              , new Object[] { source, target });
    } // GuardBytesException(). 
} // class GuardBytesException.
