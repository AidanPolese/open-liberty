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
 * Thrown when an operation is attempted on an Object in an invalid state
 * for that operation. The source of the exception is now in an error state,
 * this represents an internal error in the ObjectManager.
 */
public final class StateErrorException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -5001583364893883148L;

    /**
     * StateErrorException.
     * 
     * @param Object which is throwing this StateErrorException.
     * @param int the previous state of the source Object.
     * @param String the descriptive name of the previous state.
     */
    protected StateErrorException(Object source, int state, String stateName)
    {
        super(source,
              StateErrorException.class
              , new Object[] { source, new Integer(state), stateName });
    } // StateErrorException(). 
} // class StateErrorException.
