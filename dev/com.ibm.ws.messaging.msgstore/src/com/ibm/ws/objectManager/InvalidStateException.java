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
 * Thrown when the object manager detects that an Object is in an invalid state for the
 * operation being attempted. The operation is abandoned with no change to the Object.
 */
public final class InvalidStateException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -3269797940476314238L;

    /**
     * InvalidStateException.
     * 
     * @param Object which is throwing this InvalidStateException.
     * @param int the state of the source Object.
     * @param String the descriptive name of the state.
     */
    protected InvalidStateException(Object source, int state, String stateName)
    {
        super(source,
              InvalidStateException.class
              , new Object[] { source, new Integer(state), stateName });
    } // InvalidStateException(). 

} // End of class InvalidStateException.
