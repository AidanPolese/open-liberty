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
 * Thrown when an Objectstore is requested to reduce its file size below that which will
 * allow it to store the existing contents of the store.
 * 
 * @param ObjectStore which has been requested to reduce its size.
 * @param long the new maximum size of the store file.
 * @param long the surrent size of the store file.
 * @param long the ammount of space currently used in the store file.
 */
public final class StoreFileSizeTooSmallException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 8783023996392115290L;

    protected StoreFileSizeTooSmallException(ObjectStore source
                                             , long maximumStoreFileSize
                                             , long storeFileSizeAllocated
                                             , long storeFileSizeUsed)
    {
        super(source,
              StoreFileSizeTooSmallException.class,
              new Object[] { new Long(maximumStoreFileSize)
                            , new Long(storeFileSizeAllocated)
                            , new Long(storeFileSizeUsed) });
    } // Constructor.
} // End of class StoreFileSizeTooSmallException.
