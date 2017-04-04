package com.ibm.tx.util;
//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007,2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// DESCRIPTION:
//
// Change History:
//
// Date      Programmer    Defect   Description
// --------  ----------    ------   -----------
// 10/09/23  johawkes      663227   Ripped off from WAS.adapter
// 10/12/21  johawkes      663227.1 Perf changes
// 11/05/31  johawkes      707294   More perf changes

import java.util.AbstractSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

/**
* Provides a thread-safe hash set by delegating to java.util.concurrent.ConcurrentHashMap.
* The keys of the map constitute the set.  Values are ignored.
*/
public class ConcurrentHashSet<E> extends AbstractSet<E>
{
    /**
    * The ConcurrentHashMap containing the set.
    */
    ConcurrentHashMap<E, byte[]> map = new ConcurrentHashMap<E, byte[]>(256, 0.75f, getNumCHBuckets());

    // Calculate number of concurrent hash buckets as a factor of
    // the number of available processors.
    public static int getNumCHBuckets()
    {
        // determine number of processors
        final int baseVal = Runtime.getRuntime().availableProcessors() * 20;

        // determine next power of two
        int pow = 2;
        while (pow < baseVal)
            pow *= 2;
        return pow;
    }

    /**
    * A value to place in the map where needed.
    * The keys, not the values, of the ConcurrentHashMap constitute the set.
    */
    byte[] value = new byte[0];

    /**
    * Add an entry to the set.
    * @param entry the entry.
    * @return true if added, false if already there.
    */
    @Override
    public final boolean add(E entry)
    {
        return map.put(entry, value) == null;
    }

    /**
    * Remove an entry from the Set.
    * @param entry the entry.
    * @return true if this ConcurrentHashSet is modified, false otherwise
    */
    @Override
    public final boolean remove(Object key)
    {
        return map.remove(key) != null;         
    }

    /**
    * Check if the set contains an entry.
    * @param entry the entry.
    * @return true if the set contains the entry, false if not.
    */
    @Override
    public final boolean contains(Object entry)
    {
        return map.containsKey(entry);
    }

    /**
    * @return an iterator over the set.
    */
    @Override
    public final Iterator<E> iterator()
    {
        return map.keySet().iterator();
    }

    /**
    * @return the size of the set.
    */
    @Override
    public final int size()
    {
        return map.size();
    }
}