/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import javax.naming.Reference;

import com.ibm.ejs.util.Util;
import com.ibm.wsspi.resource.ResourceInfo;

@SuppressWarnings("serial")
public class IndirectReference extends Reference {
    final String name;
    final String bindingName;
    final ResourceInfo resourceInfo;
    final String bindingListenerName;
    final boolean defaultBinding;

    public IndirectReference(String name,
                             String bindingName,
                             String type,
                             ResourceInfo resourceInfo,
                             String bindingListenerName,
                             boolean defaultBinding) {
        super(type, IndirectJndiLookupObjectFactory.class.getName(), null);

        if (bindingName == null) {
            // This is an internal error (processors should not pass null).
            // Detect it early to prevent hard-to-diagnose problems.
            throw new IllegalArgumentException("bindingName");
        }

        this.name = name;
        this.bindingName = bindingName;
        this.resourceInfo = resourceInfo;
        this.bindingListenerName = bindingListenerName;
        this.defaultBinding = defaultBinding;
    }

    @Override
    public String toString() {
        return Util.identity(this) + '[' +
               "name=" + name +
               ", bindingName=" + bindingName +
               ", type=" + getClassName() +
               ", resourceInfo=" + resourceInfo +
               ", bindingListenerName=" + bindingListenerName +
               ", defaultBinding=" + defaultBinding +
               ']';
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    private void writeObject(ObjectOutputStream out) throws NotSerializableException {
        throw new NotSerializableException();
    }
}
