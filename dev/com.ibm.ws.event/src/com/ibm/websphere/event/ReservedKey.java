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

package com.ibm.websphere.event;

import com.ibm.ws.event.internal.ReservedKeys;

/**
 * Lightweight key implementation to help reduce the number of <code>Map</code> traversals required by mainline event processing.
 * 
 * TODO: This guy needs some serious work in the long term. Right
 * now it's a place holder for better logic.
 */
public final class ReservedKey {

    /**
     * Name of the property.
     */
    private final String name;

    /**
     * Reserved slot for the property.
     */
    private final int slot;

    /**
     * Create a context key that has a reserved slot in the Event.
     */
    public ReservedKey(String name) {
        if (name == null) {
            throw new NullPointerException("Key name must not be null");
        }
        this.name = name;
        this.slot = ReservedKeys.reserveSlot(name);
    }

    /**
     * Get the name associated with this key.
     * 
     * @return the property name used when reserving this key
     */
    public String getName() {
        return name;
    }

    /**
     * The value slot associated with this key.
     * 
     * @return the slot number
     */
    public int getSlot() {
        return slot;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        return sb.append(";name=").append(name).toString();
    }

    public int hashCode() {
        return slot;
    }

    public boolean equals(Object o) {
        ReservedKey that = null;
        if (o instanceof ReservedKey) {
            that = (ReservedKey) o;
            return this.slot == that.slot;
        }
        return false;
    }
}
