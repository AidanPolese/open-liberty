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
 * Extends the Map interface to support a set of ordered keys under the scope of
 * Transactions.
 * 
 * @See java.util.SortedMap
 * @see TreeMap
 */

public interface SortedMap extends Map
{

    /**
     * @return java.util.Comparator used by the map.
     */
    java.util.Comparator comparator();

    SortedMap subMap(Object fromKey, Object toKey);

    SortedMap headMap(Object toKey);

    SortedMap tailMap(Object fromKey);

    /**
     * Returns the first (lowest) key currently in this sorted map.
     * 
     * @param Transaction controling visibility of the Map.
     * @return Object the first key currently visible in this sorted map.
     * @throws ObjectManagerException.
     */
    Object firstKey(Transaction transaction) throws ObjectManagerException;

    /**
     * Returns the last (highest) key currently in this sorted map.
     * 
     * @param Transaction controling visibility of the Map.
     * @return Object the last key currently visible in this sorted map.
     * @throws ObjectManagerException.
     */
    Object lastKey(Transaction transaction) throws ObjectManagerException;
}
