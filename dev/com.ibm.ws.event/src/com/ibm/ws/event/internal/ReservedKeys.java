/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.event.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class ReservedKeys {

    private final static Map<String, Integer> reservedKeys = new HashMap<String, Integer>();
    private final static AtomicInteger reservedSlotCount = new AtomicInteger();

    public static synchronized int reserveSlot(final String keyName) {
        Integer slot = reservedKeys.get(keyName);
        if (slot == null) {
            slot = reservedSlotCount.getAndIncrement();
            reservedKeys.put(keyName, slot);
        }
        return slot;
    }

}
