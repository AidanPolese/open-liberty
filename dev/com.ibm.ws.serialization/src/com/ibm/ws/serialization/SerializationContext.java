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
package com.ibm.ws.serialization;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * A context for serializing objects.
 */
public interface SerializationContext {
    /**
     * Add an object replacer local to this context only.
     * 
     * @param resolver the resolver to add
     */
    void addObjectReplacer(SerializationObjectReplacer replacer);

    /**
     * Create a stream for serializing objects using this context.
     * 
     * @param output the output stream to write serialized object data
     * @return a stream for serialization
     * @throws IOException if the {@link ObjectOutputStream} constructor throws
     *             an exception
     */
    ObjectOutputStream createObjectOutputStream(OutputStream output) throws IOException;
}
