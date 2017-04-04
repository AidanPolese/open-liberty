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
package com.ibm.ws.ejbcontainer.osgi;

import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

/**
 * Thrown when an exception occurs while initializing the metadata for an
 * EJBEndpoint.
 */
@SuppressWarnings("serial")
public class EJBEndpointMetaDataException extends Exception {
    public EJBEndpointMetaDataException(Throwable cause) {
        super(cause);
    }

    private void writeObject(ObjectOutputStream out) throws NotSerializableException {
        throw new NotSerializableException();
    }
}
