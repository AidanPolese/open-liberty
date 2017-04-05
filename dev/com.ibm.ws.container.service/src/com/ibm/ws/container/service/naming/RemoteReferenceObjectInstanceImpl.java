/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.naming;

import java.util.Arrays;

/**
 *
 */
public class RemoteReferenceObjectInstanceImpl implements RemoteObjectInstance {
    private static final long serialVersionUID = -6271605053333022950L;

    private final byte[] referenceBytes;

    public RemoteReferenceObjectInstanceImpl(byte[] referenceBytes) {
        this.referenceBytes = Arrays.copyOf(referenceBytes, referenceBytes.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.clientcontainer.remote.common.object.RemoteObjectInstance#getObject(com.ibm.ws.serialization.SerializationService)
     */
    @Override
    public Object getObject() throws RemoteObjectInstanceException {
        return referenceBytes;
    }

}
