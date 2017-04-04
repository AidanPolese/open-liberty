/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.security.auth.data;

/**
 * The AuthData interface is used to obtain the user and password from the configured auth data.
 */
public interface AuthData {

    /**
     * Gets the user name as defined in the configuration.
     * 
     * @return the user name.
     */
    public String getUserName();

    /**
     * Gets the password as a char[] as defined in the configuration.
     * 
     * @return the char[] representation of the password.
     */
    public char[] getPassword();

}
