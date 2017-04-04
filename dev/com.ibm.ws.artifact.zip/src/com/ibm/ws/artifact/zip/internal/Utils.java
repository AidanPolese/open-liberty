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
package com.ibm.ws.artifact.zip.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.zip.ZipFile;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 *
 */
public class Utils {
    /**
     * @param f
     * @return
     */
    public static boolean mkdirs(final File target) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return target.mkdirs();
            }

        });
    }

    /**
     * @param f
     * @return
     */
    public static boolean setLastModified(final File target, final long lastModified) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return target.setLastModified(lastModified);
            }

        });
    }

    /**
     * @param f
     * @return
     */
    public static boolean isFile(final File target) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return target.isFile();
            }

        });
    }

    public static boolean delete(final File target) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return target.delete();
            }
        });
    }

    public static void deleteOnExit(final File target) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                target.deleteOnExit();
                return null;
            }
        });
    }

    @FFDCIgnore(PrivilegedActionException.class)
    public static FileOutputStream getOutputStream(final File target, final boolean append) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<FileOutputStream>() {
                @Override
                public FileOutputStream run() throws IOException {
                    return new FileOutputStream(target, append);
                }
            });
        } catch (PrivilegedActionException e) {
            Exception e2 = e.getException();
            if (e2 instanceof IOException)
                throw (IOException) e2;
            if (e2 instanceof RuntimeException)
                throw (RuntimeException) e2;
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * @param f
     * @return
     */
    @FFDCIgnore(PrivilegedActionException.class)
    public static ZipFile newZipFile(final File target) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<ZipFile>() {
                @Override
                public ZipFile run() throws IOException {
                    return new ZipFile(target);
                }
            });
        } catch (PrivilegedActionException e) {
            Exception e2 = e.getException();
            if (e2 instanceof IOException)
                throw (IOException) e2;
            if (e2 instanceof RuntimeException)
                throw (RuntimeException) e2;
            throw new UndeclaredThrowableException(e);
        }
    }
}