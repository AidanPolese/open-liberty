/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.cm;

import java.security.PrivilegedAction;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Gets the current value of the thread context classloader and then sets it to the specified value.
 */
@Trivial
public class GetAndSetContextClassLoader implements PrivilegedAction<ClassLoader> {
    private final ClassLoader classloader;

    public GetAndSetContextClassLoader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    @Override
    public ClassLoader run() {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classloader);
        return previous;
    }
}
