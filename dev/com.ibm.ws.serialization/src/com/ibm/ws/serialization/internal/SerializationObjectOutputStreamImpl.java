/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.serialization.internal;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.websphere.ras.annotation.Sensitive;

public class SerializationObjectOutputStreamImpl extends ObjectOutputStream implements PrivilegedAction<Void> {
    private final SerializationContextImpl context;

    public SerializationObjectOutputStreamImpl(OutputStream output, SerializationContextImpl context) throws IOException {
        super(output);
        this.context = context;

        if (context.isReplaceObjectNeeded()) {
            AccessController.doPrivileged(this);
        }
    }

    @Override
    public Void run() {
        enableReplaceObject(true);
        return null;
    }

    @Override
    @Sensitive
    protected Object replaceObject(@Sensitive Object object) throws IOException {
        return context.replaceObject(object);
    }
}
