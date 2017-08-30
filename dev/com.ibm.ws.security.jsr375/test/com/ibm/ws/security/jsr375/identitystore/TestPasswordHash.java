/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.jsr375.identitystore;

import javax.security.enterprise.identitystore.PasswordHash;

/**
 * Password hash implementation for testing.
 */
public class TestPasswordHash implements PasswordHash {

    @Override
    public String generate(char[] arg0) {
        return null;
    }

    @Override
    public boolean verify(char[] arg0, String arg1) {
        return false;
    }
}
