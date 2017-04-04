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
 * Thrown when an attempt is made to construct an ObjectStore with a name that has already been used.
 * 
 * @param ObjectStore
 *            which already uses the name.
 * @param String
 *            name attempted to be reused.
 * @param ObjectStore which is already using the name.
 */
public final class DuplicateObjectStoreNameException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -8851457471856201460L;

    protected DuplicateObjectStoreNameException(Object source,
                                                String objectStoreName,
                                                ObjectStore existingObjectStore)
    {
        super(source,
              DuplicateObjectStoreNameException.class,
              new Object[] { objectStoreName,
                            existingObjectStore });

    } // DuplicateObjectStoreNameException().
} // class DuplicateObjectStoreNameException.