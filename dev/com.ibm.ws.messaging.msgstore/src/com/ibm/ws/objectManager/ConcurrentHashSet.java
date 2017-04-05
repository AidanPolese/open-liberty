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
 * --------------- -------- -------- ------------------------------------------
 *  251161         07/04/05 gareth    Add ObjectManager code to CMVC
 *  343689         04/04/06 gareth    Modify trace output cont.
 *  607710         21/08/09 gareth    Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

import com.ibm.ws.objectManager.utils.Trace;
import com.ibm.ws.objectManager.utils.Tracing;

/**
 * A synchronized HashSet with greater parallelism.
 */
class ConcurrentHashSet extends java.util.AbstractSet
{
    private static final Class cclass = ConcurrentHashSet.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(ConcurrentHashSet.class,
                                                                     ObjectManagerConstants.MSG_GROUP_MAPS);

    private final java.util.Set[] subSets;

    /**
     * Build a set of HashSets.
     */
    public ConcurrentHashSet(int subSetCount)
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this, cclass
                        , "<init>"
                        , "subSetCount=" + subSetCount + "(int)"
                            );

        subSets = new java.util.HashSet[subSetCount];
        for (int i = 0; i < subSets.length; i++) {
            subSets[i] = new java.util.HashSet();
        } // for subSets. 

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass
                       , "<init>"
                            );
    } // Of Constructor. 

    /*
     * Compute the subSet containing a key.
     * 
     * @param Object the key to be found.
     * 
     * @return java.util.Set which could contain the key.
     */
    private final java.util.Set getSubSet(final Object key)
    {
        // Make sure the hashcode is positive.
        return subSets[(int) ((key.hashCode() & 0x7FFFFFFF) % subSets.length)];
    } // Of getSubSet().

    // --------------------------------------------------------------------------
    // Extends AbstractSet.
    // --------------------------------------------------------------------------  

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#add(java.lang.Object)
     */
    public final boolean add(Object key
                    )
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this, cclass
                        , "add"
                        , "key=" + key + "(Object)"
                            );

        boolean found = false;
        java.util.Set subSet = getSubSet(key);
        synchronized (subSet) {
            found = subSet.add(key);
        } // synchronized (subSet).

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass
                       , "add"
                       , "returns found=" + found + "(boolean)"
                            );
        return found;
    } // end of add().

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#clear()
     */
    public final void clear() {
        for (int i = 0; i < subSets.length; i++) {
            synchronized (subSets[i]) {
                subSets[i].clear();
            } // synchronized (subSets[i]).
        }
    } // Of clear(). 

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#iterator()
     */
    public java.util.Iterator iterator() {
        // TODO Bug in remove from a concurrentSet/Map iterator, it does not actually do the remove!
        java.util.Set combinedSet = new java.util.HashSet();
        if (subSets != null) {
            for (int i = 0; i < subSets.length; i++) {
                synchronized (subSets[i]) {
                    combinedSet.addAll(subSets[i]);
                } // synchronized (subSet).
            }
        }
        return combinedSet.iterator();
    } // Of iterator().

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public final boolean remove(Object key) {
        java.util.Set subSet = getSubSet(key);
        synchronized (subSet) {
            return subSet.remove(key);
        } // synchronized (subSet).
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#size()
     */
    public final int size() {
        int overallSize = 0;
        if (subSets != null) {
            for (int i = 0; i < subSets.length; i++) {
                overallSize = overallSize + subSets[i].size();
            }
        }
        return overallSize;
    } // Of size(). 

    /**
     * Short description of the object.
     * Overrides toString in AbstractMap, which conyains the whole map.
     */
    public String toString()
    {
        return new String(cclass.getName()
                          + "/" + (subSets == null ? 0 : subSets.length)
                          + "/" + Integer.toHexString(hashCode()));
    } // toString().

} // Of class ConcurrentHashSet.
