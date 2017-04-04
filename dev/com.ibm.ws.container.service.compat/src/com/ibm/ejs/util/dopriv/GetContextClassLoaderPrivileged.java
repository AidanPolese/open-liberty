/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ejs.util.dopriv;

import java.security.PrivilegedAction;

/**
 * This class gets the context classloader while in privileged mode. Its purpose
 * is to eliminate the need to use an anonymous inner class in multiple modules
 * throughout the product, when the only privileged action required is to
 * get the context classloader on the current thread.
 */
public class GetContextClassLoaderPrivileged implements PrivilegedAction<ClassLoader> {

    @Override
    public ClassLoader run() {
        return Thread.currentThread().getContextClassLoader();
    }
}
