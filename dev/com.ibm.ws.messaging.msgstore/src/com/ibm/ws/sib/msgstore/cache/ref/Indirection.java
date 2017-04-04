package com.ibm.ws.sib.msgstore.cache.ref;
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
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 27/10/03 drphill  Original
 * 225118          13/08/04 drphill  Move code from tasks to stub (simplification)
 * 227403          31/08/04 corrigk  NPE in isDiscardable() and poss others
 * 228391          01/09/04 corrigk  NPE in trace in release()
 * 270103          26/04/05 schofiel sib.msgstore.OutOfCacheSpace exceptions before mediation
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 538096          25/07/08 susana   Use getInMemorySize for spilling and persistence
 * 601995          05/08/09 gareth   PD: Misc improvements in SIB.msgstore
 * ============================================================================
 */

import java.io.IOException;

import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.utils.ras.FormattedWriter;

/**
 * This class defines the behaviour of an indirection.  These are used to manage
 * the memory burden of items. (The name is historical - there used to be an
 * instance of a class that indirectly held the item on behalf of a link.)   
 * 
 * An indirection is now an entity that implements this interface. The interface
 * allows its implementor to register with an indirection cache, and to be called 
 * back when a memory constraint is being broken.
 *
 * The Indirection is expected to hold two references on behalf of the cache. 
 * These references are synchronized by the cache and should not be synchronized 
 * by the indirection. They allow the cache to maintain a linked list of of
 * Indirections for callbacks.
 */
public interface Indirection 
{
    /**
     * This is the callback from the item cache to indicate that the 
     * discardable item should be freed. It's only used to remove STORE_NEVER items
     * from the MS when the cache of these items is full.
     */
    public void releaseIfDiscardable() throws SevereMessageStoreException;


    /**
     * @return the receivers ID. Used for identifying the receiver in trace
     * output
     */
    public long getID();

    /**
     * @return the approx in memory size of the item. Used by the cache to calculate
     * memory burden.
     */
    public int getInMemoryItemSize();

    /**
     * @return link to next item in this cache.
     * Used by the cache for maintaining a linked list of 
     * cached indirections.
     * The implementor should merely return the value provided by the 
     * cache in {@link #itemCacheSetNextLink()}. 
     * The implementor should not synchronize access to this variable.
     */
    public Indirection itemCacheGetNextLink();

    /**
     * @return link to previous item in this cache.
     * Used by the cache for maintaining a linked list of 
     * cached indirections.
     * The implementor should merely return the value provided by the 
     * cache in {@link #itemCacheSetPreviousLink()}. 
     * The implementor should not synchronize access to this variable.
     */
    public Indirection itemCacheGetPrevioustLink();

    /**
     * @param linkNext the next item in the caches list of 
     * indirections.
     * Used by the cache for maintaining a linked list of 
     * cached indirections.
     * The implementor should merely store the value provided and
     * return it to the cache in {@link #itemCacheGetNextLink()}. 
     * The implementor should not synchronize access to this variable.
     */
    public void itemCacheSetNextLink(Indirection linkNext);

    /**
     * @param linkPrevious the previous item in the caches list of 
     * indirections.
     * Used by the cache for maintaining a linked list of 
     * cached indirections.
     * The implementor should merely store the value provided and
     * return it to the cache in {@link #itemCacheGetPreviousLink()}. 
     * The implementor should not synchronize access to this variable.
     */
    public void itemCacheSetPreviousLink(Indirection linkPrevious);

    /**
     * @param item a reference to the indirection's item
     * The implementor should merely store the value provided. 
     * The implementor should not synchronize access to this variable.
     */
    public void itemCacheSetManagedReference(AbstractItem item);

    /**
     * Print a short XML description of the receiver on the given writer.
     * @param writer
     * @throws IOException
     */
    public void xmlShortWriteOn(FormattedWriter writer) throws IOException;
}
