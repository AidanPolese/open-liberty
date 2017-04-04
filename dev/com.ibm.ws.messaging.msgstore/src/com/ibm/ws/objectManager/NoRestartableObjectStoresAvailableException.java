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
 * Thrown when we attempt to store a named ManagedObject and there are no restartable
 * ObjectStores available.
 */
public final class NoRestartableObjectStoresAvailableException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 939643560396649135L;

    protected NoRestartableObjectStoresAvailableException(ObjectManager objectManager)
    {
        super(objectManager,
              NoRestartableObjectStoresAvailableException.class,
              objectManager);
    } // NoRestartableObjectStoresAvailableException().
} // class NoRestartableObjectStoresAvailableException.