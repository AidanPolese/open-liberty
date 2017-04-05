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
package com.ibm.websphere.concurrent.persistent;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Exception indicating a failure related to the persistent store.
 * The original exception must be chained as the cause.
 */
// TODO switch to proposed spec class
public class PersistentStoreException extends RuntimeException {
    private static final long serialVersionUID = -7825981788826014265L;

    /**
     * Constructs a <code>PersistentStoreException</code> with <code>null</code> as its detail message.
     * The cause is not initialized, and must subsequently be initialized by a call to <code>Throwable.initCause(java.lang.Throwable)</code>.
     */
    public PersistentStoreException() {
        super();
    }

    /**
     * Constructs a <code>PersistentStoreException</code> with the specified detail message and cause.
     * 
     * @param message the detail message (which is saved for later retrieval by the <code>Throwable.getMessage()</code> method).
     * @param cause the cause (which is saved for later retrieval by the <code>Throwable.getCause()</code> method).
     */
    public PersistentStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Disallow serialization because we want this to be replaced with a spec exception class.
     * 
     * @param out stream to which to serialize
     * @throws IOException if there is an error writing to the stream
     */
    @Trivial
    private void writeObject(ObjectOutputStream out) throws IOException {
    	throw new NotSerializableException();
    }
}
