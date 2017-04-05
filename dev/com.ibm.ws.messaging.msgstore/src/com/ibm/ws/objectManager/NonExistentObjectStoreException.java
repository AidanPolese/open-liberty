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
 * Thrown when an attempt is made to locate an object store whose
 * objectStoreIdentifier is not registered with the ObjectManager.
 * 
 * @param ObjectManagerState throwing the exception.
 * @param int identifier of the ObjectStore.
 */
public final class NonExistentObjectStoreException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -5401561769543505756L;

    protected NonExistentObjectStoreException(ObjectManagerState objectManagerState,
                                              int objectStoreIdentifier)
    {
        super(objectManagerState,
              NonExistentObjectStoreException.class,
              new Object[] { new Integer(objectStoreIdentifier) });

    } // NonExistentObjectStoreException().
} // class NonExistentObjectStoreException.
