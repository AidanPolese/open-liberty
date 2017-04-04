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
 * Thrown when an object store finds a sequence number is not unique.
 * 
 * @param source reporting the problem.
 * @param sequenceNumber the non unique sequence number.
 * @param existingToken already using the sequence number.
 */
public final class StoreSequenceException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 4022708165277331465L;

    protected StoreSequenceException(ObjectStore source,
                                     long sequenceNumber,
                                     Token existingToken)
    {
        super(source,
              StoreSequenceException.class,
              new Object[] { source,
                            new Long(sequenceNumber),
                            existingToken });
    } // StoreSequenceException().
} // class StoreSequenceException.
