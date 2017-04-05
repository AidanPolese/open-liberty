// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.1 SERV1/ws/code/utils/src/com/ibm/ws/util/CacheHashMap.java, WAS.utils, WAS80.SERV1, h1116.09 10/27/10 08:49:11
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  CacheHashMap.java
//
// Source File Description:
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F743-33811.1
//           WAS80     20101027 bkail    : New
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheHashMap<K, V>
                extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = -5771674458745168836L;

    private final int ivMaxSize;

    public CacheHashMap(int maxSize) {
        this(maxSize, 16, .75f, true);
    }

    public CacheHashMap(int maxSize, int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        ivMaxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > ivMaxSize;
    }
}
