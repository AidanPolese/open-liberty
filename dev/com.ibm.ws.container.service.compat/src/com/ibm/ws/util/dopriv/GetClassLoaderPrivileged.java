/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.util.dopriv;

import java.security.PrivilegedAction;

/**
 * This class gets a classloader of a class while in privileged mode. <p>
 *
 * Its purpose is to eliminate the need to use an anonymous inner class in
 * multiple modules throughout the product, when the only privileged action
 * required is to get the classloader of a class. <p>
 */
public class GetClassLoaderPrivileged implements PrivilegedAction<ClassLoader> {
    // Instance vars are public to allow fast setting/getting by caller if
    // this object is reused

    /**
     * Set this field to the Class you wish to invoke getClassLoader() on,
     * or pass the Class on the constructor.
     **/
    public Class<?> ivClass;

    /**
     * Constructs an instance of GetClassLoaderPrivileged that may be used
     * to invoke getClassLoader on the specified Class.
     **/
    public GetClassLoaderPrivileged(Class<?> clazz) {
        ivClass = clazz;
    }

    /**
     * PrivilegedAction run() method.
     **/
    @Override
    public ClassLoader run() {
        return ivClass.getClassLoader();
    }

} // GetClassLoaderPrivileged
