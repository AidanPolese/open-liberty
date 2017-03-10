/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.ibm.websphere.ras.ProtectedString;
import com.ibm.websphere.ras.Traceable;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.ffdc.FFDCSelfIntrospectable;

/**
 * This class wraps an instance of ProtectedString. The behavior is the same as that of ProtectedString
 * except that it can be serialized. This means that the value will be hidden from trace and FFDC, but
 * will be persisted in the local config cache.
 */
public final class SerializableProtectedString implements Serializable, Traceable, FFDCSelfIntrospectable {

    /** A perfectly valid, and very simple, value. Should we make incompatible changes, we would need to increment */
    private static final long serialVersionUID = 1L;

    /** A password object that holds null */
    public static final SerializableProtectedString NULL_PROTECTED_STRING = new SerializableProtectedString((char[]) null);

    /** A password object that holds the equivalent of the empty string */
    public static final SerializableProtectedString EMPTY_PROTECTED_STRING = new SerializableProtectedString(new char[] {});

    private transient ProtectedString protectedString;

    public SerializableProtectedString(@Sensitive char[] value) {
        this.protectedString = new ProtectedString(value);
    }

    @Trivial
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.protectedString = new ProtectedString((char[]) stream.readObject());

    }

    @Trivial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(getChars());
    }

    /** {@inheritDoc} */
    @Override
    public String[] introspectSelf() {
        return this.protectedString.introspectSelf();
    }

    /** {@inheritDoc} */
    @Override
    public String toTraceString() {
        return this.protectedString.toTraceString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;

        if (o instanceof SerializableProtectedString) {
            SerializableProtectedString other = (SerializableProtectedString) o;
            return this.protectedString.equals(other.protectedString);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.protectedString.hashCode();
    }

    @Override
    public String toString() {
        return this.protectedString.toString();
    }

    @Sensitive
    public char[] getChars() {
        return this.protectedString.getChars();
    }

    /**
     * Return true if password is either null or has no characters
     * (use to test situations where some kind of password is required)
     * 
     * @return true if password is null or has no characters.
     */
    public boolean isEmpty() {
        return this.protectedString.isEmpty();
    }
}
