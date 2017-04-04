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
import java.util.ArrayList;
import java.util.List;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.serialization.SerializationContext;
import com.ibm.ws.serialization.SerializationObjectReplacer;

public class SerializationContextImpl implements SerializationContext {
    private final SerializationServiceImpl service;
    private List<SerializationObjectReplacer> replacers;

    public SerializationContextImpl(SerializationServiceImpl service) {
        this.service = service;
    }

    @Override
    public void addObjectReplacer(SerializationObjectReplacer replacer) {
        if (replacers == null) {
            replacers = new ArrayList<SerializationObjectReplacer>();
        }
        replacers.add(replacer);
    }

    @Override
    public ObjectOutputStream createObjectOutputStream(OutputStream output) throws IOException {
        return new SerializationObjectOutputStreamImpl(output, this);
    }

    public boolean isReplaceObjectNeeded() {
        return replacers != null || service.isReplaceObjectNeeded();
    }

    /**
     * @param object the serialization object
     * @return the replaced object (if any) or the serialization object
     */
    @Sensitive
    public Object replaceObject(@Sensitive Object object) {
        if (replacers != null) {
            for (SerializationObjectReplacer replacer : replacers) {
                Object replacedObject = replacer.replaceObject(object);
                if (replacedObject != null) {
                    return replacedObject;
                }
            }
        }

        return service.replaceObject(object);
    }
}
