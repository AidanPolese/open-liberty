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
 * Thrown when an attempt is made to deregister a transaction which has a logical unit of work identifier that is not
 * registered with the ObjectManager.
 * 
 * @param Object throwing the exception.
 * @param Transaction which was not found to have been registered.
 */
public final class NonExistentTransactionException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 8517624139783773826L;

    protected NonExistentTransactionException(Object source,
                                              InternalTransaction internalTransaction)
    {
        super(source,
              NonExistentTransactionException.class,
              new Object[] { internalTransaction });
    } // NonExistentTransactionException.
} // class NonExistantTransactionException.
