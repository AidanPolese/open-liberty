/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.security.config.tss;

public final class OptionsKey {
    public final short supports;
    public final short requires;

    /**
     * @param supports
     * @param requires
     */
    public OptionsKey(short supports, short requires) {
        this.supports = supports;
        this.requires = requires;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {

        return supports + requires << 16;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OptionsKey other = (OptionsKey) obj;
        if (requires != other.requires)
            return false;
        if (supports != other.supports)
            return false;
        return true;
    }
}