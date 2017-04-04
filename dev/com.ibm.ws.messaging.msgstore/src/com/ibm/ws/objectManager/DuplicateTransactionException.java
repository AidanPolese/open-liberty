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
 * Thrown when an attempt is made to register or free a transaction which has same logical unit of work identifier as
 * one that is already registered or free.
 */
public final class DuplicateTransactionException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 8325741467489066039L;

    /**
     * Constructor
     * 
     * @param Object
     *            source of the exception.
     * @param IntenalTransaction
     *            being registered or freed.
     * @param InternalTransaction
     *            already registered or free.
     */
    protected DuplicateTransactionException(ObjectManagerState source,
                                            InternalTransaction newInternalTransaction,
                                            InternalTransaction existingInternalTransaction)
    {
        super(source,
              DuplicateTransactionException.class,
              new Object[] { source,
                            newInternalTransaction,
                            existingInternalTransaction });

    } // DuplicateTransactionException().
} // class DuplicateTransactionException.
