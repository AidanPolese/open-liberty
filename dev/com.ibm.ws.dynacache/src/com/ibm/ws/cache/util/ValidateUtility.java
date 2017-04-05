// 1.3, 4/23/08
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.util;

import com.ibm.websphere.cache.EntryInfo;

public class ValidateUtility {

    public static boolean verify = true;

    // Called by:
    //    DistributedObjectCacheAdapter
    public static void sharingPolicy(int sharingPolicy) {
        if (verify) {
            if (sharingPolicy != EntryInfo.SHARED_PULL &&
                sharingPolicy != EntryInfo.SHARED_PUSH &&
                sharingPolicy != EntryInfo.SHARED_PUSH_PULL &&
                sharingPolicy != EntryInfo.NOT_SHARED) {
                    throw new IllegalArgumentException("sharingPolicy:"+sharingPolicy);
                }
        }
    }

    // Called by:
    //    DistributedObjectCacheAdapter
    public static void priority(int priority) {
    }

    // Called by:
    //    DistributedObjectCacheAdapter
    public static void timeToLive(int timeToLive) {
    }

    // Called by:
    //    DistributedObjectCacheAdapter
    public static void objectNotNull(Object object, String name) {
        if (verify) {
            if (object == null) {
                throw new IllegalArgumentException(name+":"+object);
            }
        }
    }

    // Called by:
    //    DistributedObjectCacheAdapter
    public static void objectNotNull(Object object1, String name1, Object object2, String name2) {
        if (verify) {
            if (object1 == null || object2 == null) {
                throw new IllegalArgumentException(name1+":"+object1+"  "+name2+":"+object2);
            }
        }
    }

}


