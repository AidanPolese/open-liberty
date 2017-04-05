/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.registry.basic.internal;

import java.util.Set;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Simple container class for a group defined in the server.xml
 */
@Trivial
class BasicGroup {
    private final String name;
    private final Set<String> members;

    /**
     * BasicGroup representing the name and members of the group.
     * 
     * @param name group securityName
     * @param members group members
     */
    BasicGroup(String name, Set<String> members) {
        this.name = name;
        this.members = members;
    }

    /**
     * Return the group securityName.
     * 
     * @return group securityName
     */
    String getName() {
        return name;
    }

    /**
     * Return the group's members.
     * 
     * @return Set of all group members
     */
    Set<String> getMembers() {
        return members;
    }

    /**
     * {@inheritDoc} Equality of a BasicGroup is based only
     * on the name of the group. The members are not relevant.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicGroup)) {
            return false;
        } else {
            BasicGroup that = (BasicGroup) obj;
            return this.name.equals(that.name);
        }
    }

    /**
     * {@inheritDoc} Return the group name.
     */
    @Override
    public String toString() {
        return name + ", " + members;
    }

    /**
     * {@inheritDoc} Returns a hash of the name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
