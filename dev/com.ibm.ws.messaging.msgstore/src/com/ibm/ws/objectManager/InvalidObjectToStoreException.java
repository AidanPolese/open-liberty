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
 * Thrown when an attempt is made to store an invalid object.
 * 
 * @param ObjectStore throwing the exception.
 * @param ManagedObject which is invalid.
 */
public final class InvalidObjectToStoreException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 1623366100827054579L;

    protected InvalidObjectToStoreException(ObjectStore source,
                                            ManagedObject objectToStore)
    {
        super(source,
              InvalidObjectToStoreException.class,
              new Object[] { source,
                            objectToStore });
    } // InvalidObjectToStoreException(). 
} // class InvalidObjectToStoreException.
