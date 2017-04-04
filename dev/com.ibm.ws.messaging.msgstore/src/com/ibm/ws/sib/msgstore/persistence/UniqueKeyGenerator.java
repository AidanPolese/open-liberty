package com.ibm.ws.sib.msgstore.persistence;
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
 * Reason          Date   Origin       Description
 * --------------- ------ --------     --------------------------------------------
 *                 070403 kschloss     Original
 * 183180          160404 pradine      Check database tables on ME startup
 * 205363          280704 pradine      Redesign unique key generators
 * 251161          130405 gareth       Add ObjectManager code to CMVC
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.PersistenceException;

/**
 * A unique key generator, probably backed by a persistent mechanism as we need
 * unique keys across server restarts. The values returned are most likely in
 * sequence, but that is not guaranteed. 
 * 
 * @author kschloss
 * @author pradine
 */
public interface UniqueKeyGenerator
{
    /**
     * 
     * @return The unique name of the generator.
     */
    public String getName();

    /**
     * 
     * @return The size of a range of unique id's.
     */
    public long getRange();

    /**
     * Returns a unique numeric value across the entire life of the message store.
     * At least until the <code>long</code> value wraps around.
     * 
     * @return a value which is unique across the entire life of the
     * message store
     * 
     */
    public long getUniqueValue() throws PersistenceException;

    /**
     * Returns a unique value that is unique only to the current instance of the
     * <code>UniqueKeyGenerator</code>
     * 
     * @return A value which is unique to the current instance of the unique
     * key generator
     */
    public long getPerInstanceUniqueValue();
}
