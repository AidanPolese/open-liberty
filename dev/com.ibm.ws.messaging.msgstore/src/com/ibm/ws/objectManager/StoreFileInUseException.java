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
 * Thrown when an attempt is made to use a logFile that is already being used by another program.
 * 
 * @param ObjectStore throwing the exception.
 * @param String naming the file backing the store file that is locked.
 */
public final class StoreFileInUseException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 8495819717883794122L;

    protected StoreFileInUseException(ObjectStore source,
                                      String storeName)
    {
        super(source,
              StoreFileInUseException.class,
              new Object[] { source, storeName });
    } // StoreFileInUseException().
} // class StoreFileInUseException.
