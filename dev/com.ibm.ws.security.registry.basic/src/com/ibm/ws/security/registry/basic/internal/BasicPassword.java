/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.registry.basic.internal;

import com.ibm.websphere.ras.ProtectedString;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Simple container class for a passrod defined in the server.xml
 */
@Trivial
class BasicPassword {
    private final boolean isHashed;
    private final ProtectedString password;
    private final String hashedPassword;

    /**
     * BasicPassword holds either plain or hashed password
     * 
     * @param password password for user
     */
    BasicPassword(@Sensitive String password) {
        this (password, false);
    }

    /**
     * BasicPassword holds either plain or hashed password
     * 
     * @param password password for user
     * @param isHashed whether password string is hashed.
     */
    BasicPassword(@Sensitive String password, boolean isHashed) {
        this.isHashed = isHashed;
        if (isHashed) {
            this.password = null;
            this.hashedPassword = password;
        } else {
            ProtectedString ps = null;
            if (password != null && password.length() > 0) {
                ps = new ProtectedString(password.toCharArray());
            }
            this.password = ps;
            this.hashedPassword = null;
        }
    }

    /**
     * Return the user password
     * 
     * @return user password
     */
    boolean isHashed() {
        return isHashed;
    }

    /**
     * Return the user password
     * 
     * @return user password
     */
    ProtectedString getPassword() {
        return password;
    }

    /**
     * Return the user password
     * 
     * @return user password
     */
    String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public String toString() {
        return "****";
    }
}
