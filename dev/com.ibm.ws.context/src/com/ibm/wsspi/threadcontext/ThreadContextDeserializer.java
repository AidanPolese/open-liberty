/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.threadcontext;

import java.io.IOException;
import java.util.Map;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.context.service.serializable.ThreadContextDescriptorImpl;

/**
 * Deserializes thread context descriptors from bytes.
 */
@Trivial
public class ThreadContextDeserializer {
    /**
     * Deserializes a thread context descriptor.
     * 
     * @param bytes bytes obtained from the ThreadContextDescriptor.serialize method.
     * @param execProps execution properties.
     * @return a thread context descriptor.
     * @throws ClassNotFoundException if unable to find a class during deserialization.
     * @throws IOException if an error occurs during deserialization.
     */
    public static ThreadContextDescriptor deserialize(byte[] bytes, Map<String, String> execProps) throws ClassNotFoundException, IOException {
        return new ThreadContextDescriptorImpl(execProps, bytes);
    }
}
