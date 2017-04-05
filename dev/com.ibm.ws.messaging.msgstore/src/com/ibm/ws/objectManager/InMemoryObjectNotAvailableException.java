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
 * Thrown when a MemoryObjectStore was asked to retieve an managedObject that was not already in memory.
 * 
 */
public final class InMemoryObjectNotAvailableException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 2014135726065224050L;

    /*
     * Constructor.
     * 
     * @param Objectstore throwing the exception.
     * 
     * @param Token trying to get its ManagedObject.
     */
    protected InMemoryObjectNotAvailableException(ObjectStore source,
                                                  Token token)
    {
        super(source,
              InMemoryObjectNotAvailableException.class,
              new Object[] { source,
                            token });

    } // InMemoryObjectNotAvailableException().
} // class InMemoryObjectNotAvailableException.
