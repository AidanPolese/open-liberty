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

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Simple container class for a user defined in the server.xml
 */
@Trivial
class BasicUser {
    private final String name;
    private final BasicPassword password;

    /**
     * BasicUser representing the user name and password.
     * 
     * @param name name of user
     * @param password password for user
     */
    BasicUser(String name, @Sensitive String password) {
        this.name = name;
        this.password = new BasicPassword(password);
    }

    BasicUser(String name, BasicPassword password) {
        this.name = name;
        this.password = password;
    }

    /**
     * Return the user securityName.
     * 
     * @return user securityName
     */
    String getName() {
        return name;
    }

    /**
     * Return the user password
     * 
     * @return user password
     */
    BasicPassword getPassword() {
        return password;
    }

    /**
     * {@inheritDoc} Equality of a BasicUser is based only
     * on the name of the user. The password is not relevant.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicUser)) {
            return false;
        } else {
            BasicUser that = (BasicUser) obj;
            return this.name.equals(that.name);
        }
    }

    /**
     * {@inheritDoc} Return the name.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * {@inheritDoc} Returns a hash of the name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
