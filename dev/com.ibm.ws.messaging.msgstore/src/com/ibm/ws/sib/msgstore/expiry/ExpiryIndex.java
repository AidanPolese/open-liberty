package com.ibm.ws.sib.msgstore.expiry;
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
 * Reason           Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 18/07/03 corrigk   Original
 * 182434          11/11/03 corrigk   Reset iterator via new version of GBSTree code
 * 199808          23/04/04 corrigk   Reduce path length of expirer
 * 306998.20       09/01/06 gareth    Add new guard condition to trace statements
 * 520772          14/05/08 djvines   Make ExpiryComparator private and static
 * ============================================================================
 */

import java.util.Comparator;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.msgstore.gbs.GBSTree;
import com.ibm.ws.sib.msgstore.gbs.GBSTree.Iterator;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Provides an index for ExpirableReferences by wrapping the underlying tree
 * indexing mechanism. The operations are modelled on those of java.util.Treemap
 * but will be implemented by the use of a Generalised Binary Tree algorithm.
 */
public class ExpiryIndex
{
    private static TraceComponent tc = SibTr.register(ExpiryIndex.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    private GBSTree tree = null;
    private Iterator iterator = null;
    private int size = 0;

    /**
     * Constructor to create an empty expiry index.
     */
    public ExpiryIndex()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>");

        // Create new GBS tree with K-factor=2 and NodeWidth=10
        tree = new GBSTree(2, 10, new ExpiryComparator(), new ExpiryComparator());

        iterator = tree.iterator();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
    }

    /**
     * Add an ExpirableReference to the expiry index.
     * @param expirable an ExpirableReference.
     * @return true if the object was added to the index successfully.
     */
    public boolean put(ExpirableReference expirable)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "put", "ObjId=" + expirable.getID() + " ET=" + expirable.getExpiryTime());

        boolean reply = tree.insert(expirable);
        if (reply)
        {
            size++;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "put", "reply=" + reply);
        return reply;
    }

    /**
     * Remove an ExpirableReference from the expiry index. This method
     * removes the object referenced by the preceding call to next().
     * @return true if the object was removed from the index successfully.
     */
    public boolean remove()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "remove");

        boolean reply = iterator.remove();

        if (reply)
        {
            size--;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "remove", "reply=" + reply);
        return reply;
    }

    /**
     * Remove a specific ExpirableReference from the expiry index. This method
     * removes the object directly from the tree and does not use the iterator.
     * It does not therefore require a prior call to next().
     * @param expirable the ExpirableReference to be removed.
     * @return true if the object was removed from the index successfully.
     */
    public boolean remove(ExpirableReference expirable)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "remove", (expirable == null ? "null" : "ObjId=" + expirable.getID() + " ET=" + expirable.getExpiryTime()));

        boolean reply = tree.delete(expirable);
        if (reply)
        {
            size--;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "remove", "reply=" + reply);
        return reply;
    }

    /**
     * Re-create the iterator so that a subsequent call to next() will
     * start at the beginning of the index.
     */
    public void resetIterator()
    {
        iterator.reset();
        return;
    }

    /**
     * Return the number of objects in the tree
     * @return the number of objects
     */
    public int size()
    {
        return size;
    }

    /**
     * Return the next expiry reference from the expiry index via the iterator.
     * @return the expirable reference, or null if there are no more
     * items in the index.
     */
    public ExpirableReference next()
    {
        return(ExpirableReference) iterator.next();
    }

    /**
     * This class provides a comparator to be used by the GBS tree algorithms. It
     * compares the expiry time and (if necessary) the object IDs of two objects.
     * The same comparator is used for inserts, deletes and searches.
     */
    private static class ExpiryComparator implements Comparator
    {
        /**
         * Compare two objects and return a value representing their respective
         * collating sequence. This uses 'expiry time' as the primary key and 'object ID'
         * as the secondary key.
         * @param o1 the first object.
         * @param o2 the second object.
         * @return zero if the objects are equal, -1 if o1 is less than o2, else 1.
         */
        public int compare(Object o1, Object o2)
        {
            ExpirableReference ref1 = (ExpirableReference) o1;
            ExpirableReference ref2 = (ExpirableReference) o2;

            long time1 = ref1.getExpiryTime();
            long time2 = ref2.getExpiryTime();

            if (time1 == time2)
            {
                long id1 = ref1.getID();
                long id2 = ref2.getID();

                if (id1 == id2)
                {
                    return 0;
                }
                else if (id1 < id2)
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
            else if (time1 < time2)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }
}
