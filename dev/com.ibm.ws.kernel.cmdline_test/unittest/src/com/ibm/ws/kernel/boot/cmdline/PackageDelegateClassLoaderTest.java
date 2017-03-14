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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class PackageDelegateClassLoaderTest {

    private File packageFile;

    @Before
    public void setUp() {
        String path = PackageDelegateClassLoaderTest.class.getResource("").getPath();
        int packageIndex = path.lastIndexOf("com");
        String packagePath = path.substring(0, packageIndex);

        packageFile = new File(packagePath);
    }

    @Test
    public void testLoadClass() throws Exception {
        assertTrue("The classpath for tested javax.xml.ws.WebFault does not exists.", packageFile.exists());
        assertTrue("The classpath for tested javax.xml.ws.WebFault is not a directory.", packageFile.isDirectory());

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        Class defaultWebFaultClazz = systemClassLoader.loadClass("javax.xml.ws.WebFault");
        assertFalse("javax.ws.xml.WebFault was not load correctly, it should load the WebFault from JRE.", isMethodExist(defaultWebFaultClazz, "test"));

        List<String> packageList = new ArrayList<String>();
        packageList.add("javax.xml.ws");

        ClassLoader classLoader = new PackageDelegateClassLoader(new URL[] { packageFile.toURI().toURL() }, null, packageList);
        Class definedWebFaultClazz = classLoader.loadClass("javax.xml.ws.WebFault");
        assertTrue("javax.ws.xml.WebFault could not be found.", null != definedWebFaultClazz);
        assertTrue("javax.ws.xml.WebFault was not load correctly.", isMethodExist(definedWebFaultClazz, "test"));

    }

    private boolean isMethodExist(Class clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }

        return false;
    }
}
