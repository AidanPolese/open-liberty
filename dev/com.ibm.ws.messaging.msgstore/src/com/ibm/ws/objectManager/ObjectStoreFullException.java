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
 * Thrown when the ObjectStore is too full to hold a new ManagedObject.
 * 
 * @param ObjectStore requested to make the allocate() request.
 * @param ManagedObject requesting the allocate().
 */
public final class ObjectStoreFullException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -8191673442607535682L;

    protected ObjectStoreFullException(ObjectStore source,
                                       ManagedObject managedObjectToStore)
    {
        super(source,
              ObjectStoreFullException.class,
              new Object[] { source, managedObjectToStore });

    } // ObjectTsoreFulleexception(). 
} // End of class ObjectStoreFullException.