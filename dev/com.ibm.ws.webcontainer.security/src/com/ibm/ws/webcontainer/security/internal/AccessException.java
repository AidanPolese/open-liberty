/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

package com.ibm.ws.webcontainer.security.internal;

public class AccessException extends Exception {

    private static final long serialVersionUID = 7671805280686084541L;

    public AccessException(String msg) {
        super(msg);
    }
}
