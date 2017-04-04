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
 * Thrown when the object manager detects an attempt to unlock or replace
 * a managed object using a transaction that did not lock it.
 */
public final class InvalidTransactionException extends ObjectManagerException
{
    private static final long serialVersionUID = 7418328647854498853L;

    /**
     * @param ManagedObject being unlocked or replaced.
     * @param IinternalTransaction requesting the unlock.
     * @param TransactionLock the lock currently held.
     */
    protected InvalidTransactionException(ManagedObject source
                                          , InternalTransaction internalTransaction
                                          , TransactionLock transactionLock)
    {
        super(source,
              InvalidTransactionException.class,
              new Object[] { source, internalTransaction, transactionLock });

    } //InvalidTransactionException(). 

} // class InvalidTransactionException.
