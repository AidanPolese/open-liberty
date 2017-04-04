/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.serialization;

import java.io.IOException;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 * Allows resolving objects after deserialization. Typical scenarios are:
 *
 * <ul>
 * <li>A bundle wants to allow an object to be serialized normally, but needs
 * to modify the state of the object after deserialization (for example, to
 * reconnect a serialized Stub).
 *
 * <li>A bundle needs to deserialize an object specific to the context in which
 * it is being deserialized (for example, an EJBContext). In this case, the
 * bundle should typically use {@link SerializationContext#addObjectReplacer} to
 * add a custom serialized form, and {@link DeserializationContext#addObjectResolver} to
 * recognize the object and return a context-specific object. Note that the
 * class of the serialized form will typically need to be made visible via {@link DeserializationClassProvider}.
 * </ul>
 */
public interface DeserializationObjectResolver {
    /**
     * Resolves an object after deserialization. If the implementation does not
     * recognize the object then null should be returned.
     * <p>
     * Implementations are strongly encouraged to annotate the parameter
     * with {@link Sensitive} to avoid tracing user data.
     *
     * @param object the object being resolved
     * @return the resolved object, or null if no resolution is needed
     * @throws IOException if an error occurs resolving the object
     */
    Object resolveObject(@Sensitive Object object) throws IOException;
}
