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
package com.ibm.ws.security.appbnd.internal.delegation;

/**
 *
 */
public class NoRunAs implements com.ibm.ws.javaee.dd.appbnd.RunAs {

    @Override
    public String getUserid() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
