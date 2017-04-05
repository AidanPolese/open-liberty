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
 * Thrown if we try to start more transactions than the ObjectManager is currently capable of managing.
 * This is usuallybcause the ObjectManager has reduced the number of transactions it can start
 * because it needs to make sure checkpoints are completed before the log file fills.
 * 
 * @param ObjectManagerState throwing the exception.
 * @param long the current number of active Transactions.
 * @param long the current number of Transactions the ObjectManager can start.
 */
public final class TransactionCapacityExceededException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -5330604766707787610L;

    protected TransactionCapacityExceededException(ObjectManagerState source,
                                                   long totalTransactions,
                                                   long currentMaximumActiveTransactions)
    {
        super(source,
              TransactionCapacityExceededException.class,
              new Object[] { new Long(totalTransactions),
                            new Long(currentMaximumActiveTransactions) });
    } // TransactionCapacityExceededException().
} // class TransactionCapacityExceededException.
