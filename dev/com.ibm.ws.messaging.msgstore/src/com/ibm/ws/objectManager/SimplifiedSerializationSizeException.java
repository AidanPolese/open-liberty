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
 * Thrown when the maximum size of a SimpleSerialization object exceeds its maximum size.
 * 
 * @param Object throwing the exception.
 * @param long the expected maximum size of the serialized ManagedObject.
 * @param long the actual size of the serialized ManagedObject.
 */
public final class SimplifiedSerializationSizeException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 1261822491101875639L;

    protected SimplifiedSerializationSizeException(Object source,
                                                   long maximumSize,
                                                   long actualSize)
    {
        super(source,
              SimplifiedSerializationSizeException.class,
              new Object[] { new Long(maximumSize),
                            new Long(actualSize) });
    } // End of Constructor.
} // End of class InvalidStateException.
