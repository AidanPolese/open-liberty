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
package com.ibm.ws.classloading.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

import com.ibm.ws.classloading.LibertyClassLoader;

public abstract class LibertyLoader extends LibertyClassLoader implements DeclaredApiAccess {
    public LibertyLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected abstract Class<?> findClass(String className) throws ClassNotFoundException;

    @Override
    protected URL findResource(String resName) {
        return super.findResource(resName);
    }

    @Override
    protected Enumeration<URL> findResources(String resName) throws IOException {
        return super.findResources(resName);
    }

    public abstract Bundle getBundle();
}
