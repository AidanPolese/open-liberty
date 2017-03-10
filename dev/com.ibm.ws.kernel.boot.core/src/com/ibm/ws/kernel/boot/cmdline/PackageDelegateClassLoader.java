/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.cmdline;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class PackageDelegateClassLoader extends URLClassLoader
{
    private final List<String> packageList;

    public PackageDelegateClassLoader(URL[] urls, ClassLoader parent, List<String> packageList) {
        super(urls, parent);

        this.packageList = packageList;
    }

    /**
     * load class from the local classpath first, and the class was not found, load from parent or system classload again.
     */
    @Override
    protected Class<?> loadClass(String className, boolean resolve)
                    throws ClassNotFoundException {

        Class<?> loadedClass = null;

        synchronized (this) {
            loadedClass = findLoadedClass(className);

            if (loadedClass == null) {
                int index = className.lastIndexOf('.');
                String packageName = index > 0 ? className.substring(0, index) : "";
                if (packageList.contains(packageName)) {
                    try {
                        // first check our classpath
                        loadedClass = findClass(className);
                    } catch (ClassNotFoundException cnfe) {
                        // ignore this since we'll try the parent next
                    }
                }
            }
        }

        if (null == loadedClass) {
            // then the parent classpath
            loadedClass = super.loadClass(className, resolve);
        }

        // The resolve parameter is a legacy parameter that is effectively
        // never used as of JDK 1.1 (see footnote 1 of section 5.3.2 of the 2nd
        // edition of the JVM specification).  The only caller of this method is
        // is java.lang.ClassLoader.loadClass(String), and that method always
        // passes false, so we ignore the parameter.

        return loadedClass;
    }

}