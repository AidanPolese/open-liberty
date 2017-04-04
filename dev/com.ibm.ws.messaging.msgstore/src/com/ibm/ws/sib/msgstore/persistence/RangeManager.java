/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date    Origin     Description
 * --------------- ------  --------   --------------------------------------------
 *                 280704  pradine    Original
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.persistence;

import com.ibm.ws.sib.msgstore.PersistenceException;

/**
 * @author pradine
 *
 * Used by unique key generators to manage their persistent state
 */
public interface RangeManager {
    /**
     * Request an asynchronous update of the persistent state
     * 
     * @param generator to be updated
     */
    public void scheduleUpdate(UniqueKeyGenerator generator);

    /**
     * Test if the generator is known to the persistence layer
     * 
     * @param generator to check for
     * @return true if generator exists, false otherwise
     */
    public boolean entryExists(UniqueKeyGenerator generator);

    /**
     * Tell the persistence layer about a new generator
     * 
     * @param generator 
     * @return initial key to be used by the generator
     * @throws PersistenceException
     */
    public long addEntry(UniqueKeyGenerator generator) throws PersistenceException;
    
    /**
     * Request an immediate update to the persistent state
     * 
     * @param generator to be updated
     * @return the value that was stored prior to the update
     * @throws PersistenceException
     */
    public long updateEntry(UniqueKeyGenerator generator) throws PersistenceException;
}
