/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal;

import java.io.Serializable;

public class SerializedEJBRef implements Serializable {

    private static final long serialVersionUID = 5178157581955211788L;

    private final byte[] bytes;

    public SerializedEJBRef(byte[] serializedEJB) {
        bytes = serializedEJB;
    }

    public byte[] getSerializedEJB() {
        return bytes;
    }

}
