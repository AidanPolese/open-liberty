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
 * Thrown when an attempt is made to create a duplicate key in a map.
 * 
 * @param Map throwing the exception.
 * @param Object key which is a duplicate.
 * @param Map.Entry that already exists in the map.
 * @param Internal Transaction locking the existing antry or null.
 */
public final class DuplicateKeyException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -8673649483448257805L;

    protected DuplicateKeyException(Map source,
                                    Object key,
                                    Map.Entry entry,
                                    InternalTransaction lockingTransaction)
    {
        super(source,
              DuplicateKeyException.class,
              new Object[] { key,
                            entry,
                            lockingTransaction });
    } // DuplicateKeyException().
} // class DuplicateKeyException.
