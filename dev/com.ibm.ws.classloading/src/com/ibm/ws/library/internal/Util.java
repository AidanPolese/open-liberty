/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.library.internal;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.zip.ZipFile;

public enum Util {;
    private static final TraceComponent tc = Tr.register(Util.class);

    @FFDCIgnore(PrivilegedActionException.class)
    static boolean isArchive(final File f) {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws IOException {
                    new ZipFile(f).close();
                    return true;
                }
            });
        } catch (PrivilegedActionException e) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "The file " + f + " does not appear to be an archive: ", e.getException());
            }
            return false;
        }
    }

    static<K, V> Dictionary<K, V> copy(Dictionary<? extends K, ? extends V> props) {
        Hashtable<K, V> libraryProps = new Hashtable<K, V>();
        for (K key : Collections.list(props.keys()))
            libraryProps.put(key, props.get(key));
        return libraryProps;
    }

    static<T> Collection<T> freeze(Collection<? extends T> source) {
        return source == null ? null : source.isEmpty() ? Collections.<T>emptyList() : Collections.unmodifiableCollection(source);
    }
}
