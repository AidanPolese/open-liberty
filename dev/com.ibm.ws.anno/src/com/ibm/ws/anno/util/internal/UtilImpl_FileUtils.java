/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.util.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.jar.JarFile;

// TODO: Are there global utilities to use instead of these?

public class UtilImpl_FileUtils {
    // TODO: Change the result to 'boolean'.
    public static Boolean isFile(final File target) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.valueOf(target.isFile());
            }
        });
    }

    // TODO: Change the result to 'boolean'.
    public static Boolean isDirectory(final File target) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.valueOf(target.isDirectory());
            }
        });
    }

    public static boolean exists(final File target) {
        Boolean exists = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.valueOf(target.exists());
            }
        });
        return exists.booleanValue();
    }

    public static File[] listFiles(final File target) {
        return AccessController.doPrivileged(new PrivilegedAction<File[]>() {
            @Override
            public File[] run() {
                return target.listFiles();
            }
        });
    }

    public static FileInputStream createFileInputStream(final File target) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
                @Override
                public FileInputStream run() throws IOException {
                    return new FileInputStream(target);
                }
            });
        } catch (PrivilegedActionException e) {
            Exception innerException = e.getException();
            if (innerException instanceof IOException) {
                throw (IOException) innerException;
            } else if (innerException instanceof RuntimeException) {
                throw (RuntimeException) innerException;
            } else {
                throw new UndeclaredThrowableException(e);
            }
        }
    }

    public static JarFile createJarFile(final String jarPath) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<JarFile>() {
                @Override
                public JarFile run() throws IOException {
                    return new JarFile(jarPath);
                }
            });
        } catch (PrivilegedActionException e) {
            Exception innerException = e.getException();
            if (innerException instanceof IOException) {
                throw (IOException) innerException;
            } else if (innerException instanceof RuntimeException) {
                throw (RuntimeException) innerException;
            } else {
                throw new UndeclaredThrowableException(e);
            }
        }
    }
}
