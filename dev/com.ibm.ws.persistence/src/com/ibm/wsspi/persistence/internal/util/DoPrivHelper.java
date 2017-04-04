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
package com.ibm.wsspi.persistence.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.wsspi.persistence.InMemoryMappingFile;

@Trivial
public class DoPrivHelper {

    public static final PersistenceClassLoader newPersistenceClassLoader(final ClassLoader root,
                                                                         final Class<?>... del) {
        return AccessController.doPrivileged(new PrivilegedAction<PersistenceClassLoader>() {
            @Override
            public PersistenceClassLoader run() {
                ClassLoader[] loaders = new ClassLoader[del.length];
                for (int i = 0; i < del.length; i++)
                    loaders[i] = del[i].getClassLoader();
                return new PersistenceClassLoader(root, loaders);
            }
        });
    }

    @FFDCIgnore(PrivilegedActionException.class)
    public static final FileInputStream newFileInputStream(final File f) {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
                @Override
                public FileInputStream run() throws FileNotFoundException {
                    return new FileInputStream(f);
                }
            });
        } catch (PrivilegedActionException e) {
            return null;
        }
    }

    public static final URL newInMemoryMappingFileURL(final InMemoryMappingFile immf) {
        return AccessController.doPrivileged(new PrivilegedAction<URL>() {
            @Override
            public URL run() {
                try {
                    return new URL("persistence-service", "", 777, immf.getName());
                } catch (MalformedURLException e) {
                    // unexpected
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
