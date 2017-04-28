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
package com.ibm.ws.classloading.internal;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

class TestUtilClassLoader extends URLClassLoader {
    static final ClassLoader NULL_LOADER = new ClassLoader() {
        @Override
        protected synchronized Class<?> loadClass(String className, boolean resolveClass) throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }
    };

    final Set<String> classNamesNotToLoad = new HashSet<String>();

    TestUtilClassLoader(URL[] urls) {
        // disable delegation by passing in a parent that loads nothing
        super(urls, NULL_LOADER);
    }

    TestUtilClassLoader doNotLoad(String... names) {
        for (String name : names)
            classNamesNotToLoad.add(name);
        return this;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (classNamesNotToLoad.contains(name))
            throw new ClassNotFoundException(name);
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException normal) {
            return findSystemClass(name);
        }
    }
}