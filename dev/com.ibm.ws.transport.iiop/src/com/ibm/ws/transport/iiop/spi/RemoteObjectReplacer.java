/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.spi;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 * Allows replacing remote objects prior to being written by
 * Util.writeRemoteObject.
 */
public interface RemoteObjectReplacer {
    /**
     * Replaces remove objects prior to being written by Util.writeRemoteObject.
     * If an object is not replaced and is not already exported, it will be
     * automatically exported as RMI.
     * <p>
     * Implementations are strongly encouraged to annotate the parameter
     * and return value with {@link Sensitive} to avoid tracing user data.
     *
     * @param object the object being written
     * @return a remote object reference, or null if no replacement is needed
     */
    @Sensitive
    Object replaceRemoteObject(@Sensitive Object object);
}
